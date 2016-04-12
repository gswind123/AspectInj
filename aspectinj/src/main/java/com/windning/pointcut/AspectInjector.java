package com.windning.pointcut;

import java.util.Set;

/**
 * The aspect-join-point-injecting interface
 * NOTE: <code>before</code>/<code>after</code> should not be called by users
 */
public class AspectInjector {
    private static PointCutEntry mPointCutEntry = new PointCutEntry();

    /**
     * Register point cuts.All user-defined point cuts will not
     * work util this method is invoked.
     */
    public static void weave(PointCutRegistery registery) {
        if(registery != null) {
            registery.register(mPointCutEntry);
        }
    }

    private static boolean mEnabled = true;

    /**
     * Set if enable the injected point cuts
     * If disabled, the point cuts woundn't be invoked.
     * @param isEnable
     */
    public static void enablePointCuts(boolean isEnable) {
        mEnabled = isEnable;
    }

    public static boolean isPointCutsEnable() {
        return mEnabled;
    }

    private static Set<PointCut> selectPointCuts(String position) {
        return mPointCutEntry.get(position);
    }

    /**
     * @param isStatic
     *      If the method doesn't have a "this" of the top level instance,it is static,
     *      The top level instance is the instance of the class annotated.
     */
    public static boolean before(boolean isStatic, Object... args) {
        if(!mEnabled) {
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
        Set<PointCut> cuts = selectPointCuts(position);
        if(cuts == null) {
            return true;
        } else {
            boolean res = true;
            for(PointCut cut : cuts) {
                res &= cut.onBefore(self, methodArgs);
            }
            return res;
        }
    }

    public static void after(boolean isStatic, Object... args) {
        if(!mEnabled) {
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
        Set<PointCut> cuts = selectPointCuts(position);
        if(cuts != null) {
            for(PointCut cut : cuts) {
                cut.onAfter(self, methodArgs);
            }
        }
    }
}
