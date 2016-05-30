package org.windning.pointcut;

/**
 * Implement #register and call "AspectInjector.weave" to add point cuts.
 */
public interface PointCutRegistry {
    /**
     * @param entry Use this to manipulate your own point cuts
     */
    void register(PointCutEntry entry);
}
