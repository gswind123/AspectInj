package org.windning.analyze.util;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;

import org.windning.analyze.model.TypeEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Used to resolve Compatibility on JAVA7 & JAVA8
 * Created by yw_sun on 2016/5/30.
 */
public class CompatUtil {

    static public boolean isPrimeTreeVoid(JCTree.JCPrimitiveTypeTree primeTree) {
        Object typeTagVoid = null;
        try{
            Class<?> typeTagCls = Class.forName("com.sun.tools.javac.code.TypeTag");
            Method valueOfMethod = typeTagCls.getDeclaredMethod("valueOf", String.class);
            typeTagVoid = valueOfMethod.invoke(typeTagCls, "VOID");
        }catch(Exception e) {}
        Integer typeTagsVoid = null;
        try{
            Class<?> typeTagsCls = Class.forName("com.sun.tools.javac.code.TypeTags");
            Field typeTagsField = typeTagsCls.getDeclaredField("VOID");
            typeTagsVoid = typeTagsField.getInt(null);
        } catch(Exception e){}

        Object type = null;
        try{
            Field typeTagField = JCTree.JCPrimitiveTypeTree.class.getDeclaredField("typetag");
            typeTagField.setAccessible(true);
            type = typeTagField.get(primeTree);
        }catch(Exception e) {}
        if(type == null) {
            return false;
        }

        if(typeTagVoid != null) {
            return type == typeTagVoid;
        }
        if(typeTagsVoid != null) {
            return typeTagsVoid.equals(type);
        }
        return false;
    }

    static public JCTree.JCLiteral makeLiteral(TreeMaker treeMaker, TypeEnum type, Object value) {
        Class<?> typeTagCls = null;
        Class<?> typeTagsCls = null;
        try{
            typeTagCls = Class.forName("com.sun.tools.javac.code.TypeTag");
        }catch(ClassNotFoundException e) {}
        try{
            typeTagsCls = Class.forName("com.sun.tools.javac.code.TypeTags");
        } catch(ClassNotFoundException e){}

        if(typeTagCls != null) {
            try {
                return makeLiteralByTypeTag(typeTagCls, treeMaker, type, value);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if(typeTagsCls != null) {
            try {
                return makeLiteralByTypeTags(typeTagsCls, treeMaker, type, value);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    static private JCTree.JCLiteral makeLiteralByTypeTag(Class<?> typeTagCls,
                                                         TreeMaker treeMaker, TypeEnum type, Object value)
            throws NoSuchMethodException {
        Method literalMethod = null;
        try {
            literalMethod = TreeMaker.class.getDeclaredMethod("Literal", typeTagCls, Object.class);
            literalMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("Literal method not found in TypeTag version");
        }
        Object typeTagEnum = null;
        try{
            Method valueOfMethod = typeTagCls.getDeclaredMethod("valueOf", String.class);
            typeTagEnum = valueOfMethod.invoke(typeTagCls, type.getName());
        }catch (Exception e) {
            throw new NoSuchMethodException("TypeTag <"+type.getName()+"> couldn't be found in TypeTag version");
        }

        JCTree.JCLiteral literal = null;
        try {
            literal = (JCTree.JCLiteral)literalMethod.invoke(treeMaker, typeTagEnum, value);
        } catch(Exception e) {
            throw new NoSuchMethodException("Literal method invocation failed in TypeTag: "+e.getMessage());
        }
        return literal;
    }

    static private JCTree.JCLiteral makeLiteralByTypeTags(Class<?> typeTagsCls,
                                                         TreeMaker treeMaker, TypeEnum type, Object value)
            throws NoSuchMethodException {
        Method literalMethod = null;
        try{
            literalMethod = TreeMaker.class.getDeclaredMethod("Literal", int.class, Object.class);
            literalMethod.setAccessible(true);
        }catch(NoSuchMethodException e){
            throw new NoSuchMethodException("Literal method not found in TypeTags version");
        }
        int typeTagsValue;
        try{
            Field typeTagsField = typeTagsCls.getDeclaredField(type.getName());
            typeTagsValue = typeTagsField.getInt(null);
        }catch(Exception e) {
            throw new NoSuchMethodException("Literal method invocation failed in TypeTags: "+e.getMessage());
        }

        JCTree.JCLiteral literal = null;
        try {
            literal = (JCTree.JCLiteral)literalMethod.invoke(treeMaker, typeTagsValue, value);
        } catch(Exception e) {
            throw new NoSuchMethodException("Literal method invocation failed in TypeTags: "+e.getMessage());
        }
        return literal;
    }
}
