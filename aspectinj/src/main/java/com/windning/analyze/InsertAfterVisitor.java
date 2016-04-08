package com.windning.analyze;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

/**
 * This visitor is used to insert "after" method before each "return" node
 * in the current method block.The "return" node of inner classes and methods
 * woundn't get effected
 */
public class InsertAfterVisitor extends TreeTranslator{
    private int mLevelIndex = -1;

    private JCTree.JCMethodDecl mTarget;
    private JCTree.JCMethodInvocation mInvocation;
    private TreeMaker mTreeMaker;
    private Names mNameTable;

    private int mInsertCtr = 0;
    public int getInsertCount() {
        return mInsertCtr;
    }

    private boolean canDoInsert() {
        return mLevelIndex == 0;
    }

    public InsertAfterVisitor(TreeMaker maker, Names names,
                              JCTree.JCMethodDecl target, JCTree.JCMethodInvocation afterInvocation) {
        mTarget = target;
        mInvocation = afterInvocation;
        mTreeMaker = maker;
        mNameTable = names;
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl method) {
        mLevelIndex++;
        super.visitMethodDef(method);
        mLevelIndex--;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl cls) {
        mLevelIndex++;
        super.visitClassDef(cls);
        mLevelIndex--;
    }

    @Override
    public void visitReturn(JCTree.JCReturn ret) {
        super.visitReturn(ret);
        if(canDoInsert()) {
            JCTree.JCStatement afterStatment = mTreeMaker.Exec(mInvocation);
            List<JCTree.JCStatement> stats = List.of(afterStatment, ret);
            this.result = mTreeMaker.Block(0, stats);
            mInsertCtr++;
        }
    }
}
