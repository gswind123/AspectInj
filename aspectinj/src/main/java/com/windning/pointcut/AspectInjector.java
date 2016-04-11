package com.windning.pointcut;

import java.util.Map;

/**
 * The aspect-join-point-injecting interface
 * NOTE: {#before}/{#after} should not be called by users
 */
public class AspectInjector {
    private static Map<String, PointCut> sPointCutMap;

    /**
     * Register point cuts.All user-defined point cuts will not
     * work util this method is invoked.
     */
    public static void weave(PointCutRegistery registery) {
        sPointCutMap = registery.register();
    }

    private static PointCut selectPointCut(String position) {
        if(sPointCutMap != null) {
            return sPointCutMap.get(position);
        } else {
            return null;
        }
    }

    /**
     * @param isStatic
     *      If the method doesn't have a "this" of the top level instance,it is static,
     *      The top level instance is the instance of the class annotated.
     */
    public static boolean before(boolean isStatic, Object... args) {
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
        PointCut cut = selectPointCut(position);
        return cut == null ? true : cut.onBefore(self, methodArgs);
    }
    public static void after(boolean isStatic, Object... args) {
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
        PointCut cut = selectPointCut(position);
        if(cut != null) {
            cut.onAfter(self, methodArgs);
        }
    }
}
