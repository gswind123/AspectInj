package org.windning.pointcut;

/**
 * Override this to add process logic to some join point
 */
public class PointCut {
    public boolean onBefore(Object self, Object[] args){
        return true;
    }
    public void onAfter(Object self, Object[] args){

    }
}
