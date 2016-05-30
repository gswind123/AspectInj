package org.windning.annotation.processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import org.windning.analyze.TopClassAnalyzor;
import org.windning.annotation.JoinPoints;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Main annotation processor for join points
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JoinPointProcessor extends AbstractProcessor {
    private ProcessingEnvironment mEnv = null;
    private Trees mTrees;
    private TreeMaker mMaker;
    private Messager mMessager;
    private Names mNames;

    @Override
    public void init(ProcessingEnvironment env) {
        super.init(env);
        if(!(env instanceof JavacProcessingEnvironment)) {
            //Only Javac do we support temporarily
            return ;
        }
        mEnv = env;
        mMessager = env.getMessager();
        mTrees = Trees.instance(env);
        Context context = ((JavacProcessingEnvironment)env).getContext();
        mMaker = TreeMaker.instance(context);
        mNames = Names.instance(context);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<String>();
        set.add(JoinPoints.class.getName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(mEnv == null) {
            return false;
        }
        if(!roundEnv.processingOver()) {
            Set<? extends Element> elements = roundEnv.getRootElements();
            for(Element each : elements) {
                Annotation a = each.getAnnotation(JoinPoints.class);
                if(a != null) {
                    JCTree tree = (JCTree)mTrees.getTree(each);
                    if(tree instanceof JCTree.JCClassDecl) {
                        TreeTranslator visitor = new TopClassAnalyzor((JCTree.JCClassDecl)tree, mMaker, mNames, mMessager);
                        tree.accept(visitor);
                    }
                }
            }
        }
        return true;
    }
}
