/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import necesse.engine.util.GameLinkedList;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.regionSystem.RegionTracker;

public interface RegionTrackerGetter<T> {
    public RegionTracker<T> getRegionTracker();

    default public PointSetAbstract<?> updateRegion(RegionTrackerList.RegionListGetter<T> regionsGetter, RegionTrackerList.RegionListGetter<T> saveToRegionGetter, GameLinkedList<? super T> noRegionList) {
        RegionTracker<? super T> regionTracker = this.getRegionTracker();
        if (regionTracker != null) {
            return regionTracker.updateRegion(regionsGetter, saveToRegionGetter, noRegionList);
        }
        return null;
    }
}

