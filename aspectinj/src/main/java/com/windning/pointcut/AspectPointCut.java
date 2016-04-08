package com.windning.pointcut;

/**
 * The aspect-join-point-injecting interface
 * NOTE: {#before}/{#after} should not be called by users
 */
public class AspectPointCut {
    private static PointCutProcessor sProcessor = null;

    public static void weave(PointCutProcessor processor) {
        sProcessor = processor;
    }

    /**
     * @param isStatic
     *      If the method doesn't have a "this" of the top level instance,it is static,
     *      The top level instance is the instance of the class annotated.
     */
    public static boolean before(boolean isStatic, Object... args) {
        if(sProcessor == null) {
            return true;
        }
        int minArgNum = 2;
        if(isStatic) {
            minArgNum = 1;
        }
        if(args == null || args.length <minArgNum) {
            //Invalid arg number
            return true;
        }
        Object posObj = args[args.length - 1];
        if(!(posObj instanceof String)) {
            //Position invalid
            return true;
        }
        Object self = null;
        int methodArgStart = 0;
        if(!isStatic) {
            self = args[0];
            methodArgStart = 1;
        }
        String position = (String)posObj;
        Object[] methodArgs = new Object[args.length - minArgNum];
        for(int i=methodArgStart,j=0;i<args.length-1;i++,j++) {
            methodArgs[j] = args[i];
        }
        return sProcessor.onBefore(position, self, methodArgs);
    }
    public static void after(boolean isStatic, Object... args) {
        if(sProcessor == null) {
            return ;
        }
        int minArgNum = 2;
        if(isStatic) {
            minArgNum = 1;
        }
        if(args == null || args.length <minArgNum) {
            //Invalid arg number
            return ;
        }
        Object posObj = args[args.length - 1];
        if(!(posObj instanceof String)) {
            //Position invalid
            return ;
        }
        Object self = null;
        int methodArgStart = 0;
        if(!isStatic) {
            self = args[0];
            methodArgStart = 1;
        }
        String position = (String)posObj;
        Object[] methodArgs = new Object[args.length - minArgNum];
        for(int i=methodArgStart,j=0;i<args.length-1;i++,j++) {
            methodArgs[j] = args[i];
        }
        sProcessor.onAfter(position, self, methodArgs);
    }
}
