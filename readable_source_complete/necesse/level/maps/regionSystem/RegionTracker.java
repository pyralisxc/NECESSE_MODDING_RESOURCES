/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import necesse.engine.util.GameLinkedList;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.manager.RegionTrackerList;

public interface RegionTracker<T> {
    public PointSetAbstract<?> updateRegion(RegionTrackerList.RegionListGetter<T> var1, RegionTrackerList.RegionListGetter<T> var2, GameLinkedList<? super T> var3);

    public void clearElements();
}

