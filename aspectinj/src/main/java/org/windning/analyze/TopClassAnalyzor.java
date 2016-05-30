package org.windning.analyze;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import org.windning.analyze.model.TypeEnum;
import org.windning.analyze.util.AnalyzeUtil;
import org.windning.analyze.util.CompatUtil;

import java.util.ArrayList;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Class analyzor of the top level
 */
public class TopClassAnalyzor extends TreeTranslator {
    private JCClassDecl mRootClass;
    private TreeMaker mTreeMaker;
    private Names mNameTable;
    private Messager mMessager;

    public TopClassAnalyzor(JCClassDecl rootClass, TreeMaker treeMaker, Names names, Messager msg) {
        mRootClass = rootClass;
        mTreeMaker = treeMaker;
        mNameTable = names;
        mMessager = msg;
    }

    private int mAnonyIndex = -1;
    private ArrayList<ContextElement> mCxtStack = new ArrayList<ContextElement>();
    private void addStack(JCTree tree) {
        ContextElement element = new ContextElement();
        element.content = tree;
        if(AnalyzeUtil.isTreeAnonymous(tree)) {
            element.anonyIndex = mAnonyIndex + 1;
        } else {
            element.anonyIndex = mAnonyIndex;
        }
        mAnonyIndex = -1; //new anony ctr
        mCxtStack.add(element);
    }
    private ContextElement popStack() {
        ContextElement element = mCxtStack.remove(mCxtStack.size()-1);
        mAnonyIndex = element.anonyIndex; //pop anony ctr
        return element;
    }

    @Override
    public void visitClassDef(JCClassDecl cls) {
        addStack(cls);
        super.visitClassDef(cls);
        popStack();
    }

    @Override
    public void visitMethodDef(JCMethodDecl method) {
        addStack(method);
        super.visitMethodDef(method);
        try{
            if("<init>".equals(method.getName().toString())) {
                throw new IllegalAccessException("Can't modify <init> function");
            }
            ListBuffer<JCTree.JCStatement> stateList = new ListBuffer<JCTree.JCStatement>();
            JCBlock funcBody = method.getBody();
            /**
             * While injecting "before" & "after", it follow the following order:
             * 1.Add "if before else return" logic;
             * 2.Set states in (1.) to the body
             * 3.Visit method body to insert "after"
             */
            //1.Insert before method
            JCMethodInvocation invokeBefore = generateBeforeInvocation(method);
            /**
             * add "before" method like this:
             * {
             *     if(before(...)) {
             *         raw method block;
             *     } else {
             *         return emptyReturn;
             *     }
             * }
             * If return type is "void", "else" part will not exist;
             */
            JCReturn elseReturn = AnalyzeUtil.makeEmptyReturn(mTreeMaker, mNameTable, method);
            JCBlock thenBefore = mTreeMaker.Block(0, funcBody.getStatements());
            JCIf ifBefore = mTreeMaker.If(invokeBefore, thenBefore, elseReturn);
            stateList.append(ifBefore);

            //2.Set "if" to method body
            funcBody.stats = stateList.toList();

            //3.Insert "after" method.It needs to be inserted before every "return"
            JCMethodInvocation invokeAfter = generateAfterInvocation(method);
            InsertAfterVisitor afterVisitor = new InsertAfterVisitor(mTreeMaker, mNameTable,
                    method, invokeAfter);
            method.accept(afterVisitor);
            if(afterVisitor.getInsertCount() == 0) {
                mMessager.printMessage(Diagnostic.Kind.WARNING, AnalyzeUtil.getMethodSignature(mCxtStack, method)+"\t"+"get no after");
            }

        }catch (Exception e){

        }finally {
            popStack();
        }
    }

    /**
     * XXX: #generateBeforeInvocation #generateAfterInvocation can be merged to one method
     */
    private JCMethodInvocation generateBeforeInvocation(JCMethodDecl method) {
        //First,access before method
        JCTree.JCFieldAccess accessBefore = AnalyzeUtil.makeAccess(mTreeMaker, mNameTable,
                "org", "windning", "pointcut", "AspectInjector", "before");

        //Then,add arguments
        boolean isStatic = AnalyzeUtil.isContextStatic(mCxtStack);
        ListBuffer<JCTree.JCExpression> beforeArgs = new ListBuffer<JCTree.JCExpression>();
        beforeArgs.append(CompatUtil.makeLiteral(mTreeMaker, TypeEnum.BOOLEAN, isStatic?1:0));
        if(!isStatic) {
            beforeArgs.append(
                    AnalyzeUtil.makeAccess(mTreeMaker, mNameTable, mRootClass.getSimpleName().toString(), "this"));
        }
        for(JCTree.JCVariableDecl param : method.params) {
            beforeArgs.append(mTreeMaker.Ident(param.getName()));
        }
        String methodSig = AnalyzeUtil.getMethodSignature(mCxtStack, method);

        beforeArgs.append(CompatUtil.makeLiteral(mTreeMaker, TypeEnum.CLASS, methodSig));

        //Finally,make the invocation
        JCTree.JCMethodInvocation invokeBefore = mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                accessBefore, beforeArgs.toList());
        return invokeBefore;
    }

    private JCMethodInvocation generateAfterInvocation(JCMethodDecl method) {
        //First,access after method
        JCTree.JCFieldAccess accessAfter = AnalyzeUtil.makeAccess(mTreeMaker, mNameTable,
                "org", "windning", "pointcut", "AspectInjector", "after");

        //Then,add arguments
        boolean isStatic = AnalyzeUtil.isContextStatic(mCxtStack);
        ListBuffer<JCTree.JCExpression> afterArgs = new ListBuffer<JCTree.JCExpression>();
        afterArgs.append(CompatUtil.makeLiteral(mTreeMaker, TypeEnum.BOOLEAN, isStatic ? 1 : 0));
        if(!isStatic) {
            afterArgs.append(
                    AnalyzeUtil.makeAccess(mTreeMaker, mNameTable, mRootClass.getSimpleName().toString(), "this"));
        }
        for(JCTree.JCVariableDecl param : method.params) {
            afterArgs.append(mTreeMaker.Ident(param.getName()));
        }
        String methodSig = AnalyzeUtil.getMethodSignature(mCxtStack, method);
        afterArgs.append(CompatUtil.makeLiteral(mTreeMaker, TypeEnum.BOOLEAN, isStatic ? 1 : 0));

        //Finally,make the invocation
        JCTree.JCMethodInvocation invokeAfter = mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                accessAfter, afterArgs.toList());
        return invokeAfter;
    }

}
