package org.windning.analyze;

import com.sun.tools.javac.tree.JCTree;

/**
 * The class is used to describe a class/method block
 * This is useful when analyzing inner class and anonymous class
 */
public class ContextElement {
    public JCTree content;

    /**
     * The index in the parent context if the element is anonymous
     * NOTE:This index gets a value even if the element is not anonymous
     */
    public int anonyIndex;
}
