package com.windning.pointcut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Entry of the PointCut set
 */
public class PointCutEntry {
    private Map<String, HashSet<PointCut>> mPointCutMap = new HashMap<String, HashSet<PointCut>>();

    public HashSet<PointCut> get(String position) {
        return mPointCutMap.get(position);
    }

    /**
     * Add a point cut to be invoked at the position
     * NOTE:
     *  - A position can have more than one point cuts.They will all be invoked,
     *    but NOT in a preserved order(e.g.the added order).
     *  - A position will do its main body only if all its point cuts return true,
     *    which means you can disable a position's main body by adding just one return-false-before cut.
     * @param position
     * @param pointCut
     */
    public void add(String position, PointCut pointCut) {
        HashSet<PointCut> set = get(position);
        if(set == null) {
            set = new HashSet<PointCut>();
            mPointCutMap.put(position, set);
        }
        set.add(pointCut);
    }

    /**
     * Remove a point cut from a position.
     * @param position
     * @param pointCut
     * @return <tt>true</tt> if the pointCut has been binded.
     */
    public boolean remove(String position, PointCut pointCut) {
        HashSet<PointCut> set = get(position);
        if(set != null) {
            return set.remove(pointCut);
        } else {
            return false;
        }
    }

    /**
     * Remove all point cuts from a position
     * @param position
     * @return the point cuts of the position, <tt>null</tt> if the position is clean.
     */
    public HashSet<PointCut> remove(String position) {
        return mPointCutMap.remove(position);
    }
}
