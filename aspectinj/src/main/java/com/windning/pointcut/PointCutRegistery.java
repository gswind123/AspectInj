package com.windning.pointcut;

import java.util.Map;

/**
 * Implement #register and call "AspectInjector.weave" to add point cuts.
 */
public interface PointCutRegistery {
    /**
     * Implement this interface to add your own point cuts.
     * @return a map: the KEY will be the position descriptor, the VALUE will be PointCut
     */
    Map<String, PointCut> register();
}
