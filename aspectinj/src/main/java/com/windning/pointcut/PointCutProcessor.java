package com.windning.pointcut;

/**
 * Used for users to add their own before and after method
 * Implement this can call "AspectPointCut.weave"
 */
public interface PointCutProcessor {
    boolean onBefore(String position, Object self, Object[] args);
    void onAfter(String position, Object self, Object[] args);
}
