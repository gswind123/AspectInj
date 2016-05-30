package org.windning.analyze.util;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import org.windning.analyze.ContextElement;
import org.windning.analyze.model.TypeEnum;

import java.util.ArrayList;

import javax.lang.model.type.TypeKind;

/**
 * Util for analyze operations
 */
public class AnalyzeUtil {

    /**
     * Make access node of AST by a plain path
     * @param maker
     * @param names
     * @param path {"com", "windning", "annotation"} => "com.windning.annotation"
     */
    public static JCTree.JCFieldAccess makeAccess(TreeMaker maker, Names names, String... path) {
        if(path == null || path.length < 2) {
            return null;
        }
        JCTree.JCExpression selected = maker.Ident(names.fromString(path[0]));
        for(int i=1;i<path.length;i++) {
            selected = maker.Select(selected, names.fromString(path[i]));
        }
        if(selected instanceof JCTree.JCFieldAccess) {
            return (JCTree.JCFieldAccess)selected;
        } else {
            return null;
        }
    }

    public static boolean isMethodStatic(JCTree.JCMethodDecl method) {
        return (method.mods.flags & Flags.STATIC) != 0;
    }
    public static boolean isClassStatic(JCTree.JCClassDecl cls) {
        return (cls.mods.flags & Flags.STATIC) != 0;
    }

    /**
     * NOTE:If tree is neither class nor method, it is not static
     * @param tree Mostly is a class or method
     */
    public static boolean isTreeStatic(JCTree tree) {
        boolean isStatic = false;
        if(tree instanceof JCTree.JCMethodDecl) {
            isStatic = isMethodStatic((JCTree.JCMethodDecl)tree);
        } else if(tree instanceof JCTree.JCClassDecl) {
            isStatic = isClassStatic((JCTree.JCClassDecl)tree);
        }
        return isStatic;
    }

    /**
     * Judge whether the current context is of a "this" of the top object.
     * @param contextStack The method and class stack of the top level AST
     */
    public static boolean isContextStatic(ArrayList<ContextElement> contextStack) {
        for(ContextElement element : contextStack) {
            if(isTreeStatic(element.content)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Judge if the tree is anonymous
     * NOTE:if the tree is neither a class nor a method, it is not anonymous
     * @param tree Mostly is a class or method
     */
    public static boolean isTreeAnonymous(JCTree tree) {
        String name = getTreeName(tree,false);
        return name == null || name.length() == 0;
    }

    /**
     * NOTE:Mostly the tree should be a method or class
     * @param tree
     * @param useFullName true:get the full name of the tree if it's a class|false:get simple name
     * @return the name
     */
    public static String getTreeName(JCTree tree, boolean useFullName) {
        String name = "";
        if(tree instanceof JCTree.JCClassDecl) {
            if(useFullName) {
                Symbol.ClassSymbol sym = ((JCTree.JCClassDecl) (tree)).sym;
                if(sym != null)
                name = ((JCTree.JCClassDecl) (tree)).sym.fullname.toString();
            } else {
                name = ((JCTree.JCClassDecl) (tree)).getSimpleName().toString();
            }
        } else if(tree instanceof JCTree.JCMethodDecl) {
            name = ((JCTree.JCMethodDecl)(tree)).getName().toString();
        } else {
            name = tree.toString();
        }
        return name;
    }

    /**
     * Generate a method signature
     * NOTE:
     *  For an anonymous class, its name will be "class#[index]",in which [index] represents
     *  the declaration order in its upper-level class/method, started from 0.
     * @param contextStack The context stack of the method
     * @param method The declaration node of the target method
     * @return the method's signature
     */
    public static String getMethodSignature(ArrayList<ContextElement> contextStack, JCTree.JCMethodDecl method) {
        StringBuilder sigBuilder = new StringBuilder();
        int stackSize = contextStack.size();
        for(int i=0;i<stackSize;i++) {
            ContextElement element = contextStack.get(i);
            String name = "";
            if(isTreeAnonymous(element.content)) {
                if(element.content instanceof JCTree.JCMethodDecl) {
                    //Technically this should not be possible.
                    name = "method";
                } else if(element.content instanceof JCTree.JCClassDecl) {
                    name = "class";
                }
                name += "#" + element.anonyIndex;
            } else {
                if(i == 0) {
                    //Only the first class can use full name
                    name = getTreeName(element.content, true);
                } else {
                    name = getTreeName(element.content ,false);
                }
            }
            sigBuilder.append(name);
            if(i != stackSize - 1) {
                sigBuilder.append('.');
            }
        }
        //the current method must be on the stack's top, just add param types
        sigBuilder.append('(');
        List<JCTree.JCVariableDecl> paramList = method.getParameters();
        int paramSize = paramList.size();
        for(int i=0;i<paramSize;i++) {
            JCTree.JCVariableDecl decl = paramList.get(i);
            sigBuilder.append(decl.vartype.toString());
            if(i != paramSize - 1) {
                sigBuilder.append(',');
            }
        }
        sigBuilder.append(')');
        return sigBuilder.toString();
    }

    /**
     * Create a "return" node for a method
     */
    public static JCTree.JCReturn makeEmptyReturn(TreeMaker maker, Names nameTable, JCTree.JCMethodDecl method) {
        JCTree returnType = method.getReturnType();
        JCTree.JCExpression expr = null;
        if(returnType instanceof JCTree.JCPrimitiveTypeTree) {
            JCTree.JCPrimitiveTypeTree primeTree = (JCTree.JCPrimitiveTypeTree) returnType;
            if(CompatUtil.isPrimeTreeVoid(primeTree)) {
                return null;
            }
            TypeKind kind = primeTree.getPrimitiveTypeKind();
            if(kind.isPrimitive()) {
                TypeEnum type = null;
                switch(kind) {
                case BOOLEAN:
                    type = TypeEnum.BOOLEAN; break;
                case BYTE:
                    type = TypeEnum.BYTE; break;
                case SHORT:
                    type = TypeEnum.SHORT; break;
                case INT:
                    type = TypeEnum.INT; break;
                case LONG:
                    type = TypeEnum.LONG; break;
                case CHAR:
                    type = TypeEnum.CHAR; break;
                case FLOAT:
                    type = TypeEnum.FLOAT; break;
                case DOUBLE:
                    type = TypeEnum.DOUBLE; break;
                }
                if(type != null) {
                    expr = CompatUtil.makeLiteral(maker, type, 0);
                }
            }
        }
        if(expr == null) {
            expr = CompatUtil.makeLiteral(maker, TypeEnum.BOT, null);
        }
        JCTree.JCReturn retNode = maker.Return(expr);
        return retNode;
    }
}
