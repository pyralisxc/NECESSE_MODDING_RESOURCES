/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import necesse.engine.util.GameLinkedList;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.SingletonPointSet;
import necesse.entity.TileEntity;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionTracker;

public abstract class TileEntityRegionPositionTracker<T extends TileEntity>
implements RegionTracker<T> {
    protected final T entity;
    private final Object regionLock = new Object();
    protected GameLinkedList.Element regionElement;

    public TileEntityRegionPositionTracker(T entity) {
        this.entity = entity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PointSetAbstract<?> updateRegion(RegionTrackerList.RegionListGetter<T> regionsGetter, RegionTrackerList.RegionListGetter<T> saveToRegionGetter, GameLinkedList<? super T> noRegionList) {
        if (this.isDisposed()) {
            return null;
        }
        Level level = ((TileEntity)this.entity).getLevel();
        if (level == null) {
            Object object = this.regionLock;
            synchronized (object) {
                if (this.regionElement != null) {
                    this.regionElement.remove();
                    this.regionElement = null;
                }
            }
            return null;
        }
        int regionX = level.regionManager.getRegionCoordByTile(((TileEntity)this.entity).tileX);
        int regionY = level.regionManager.getRegionCoordByTile(((TileEntity)this.entity).tileY);
        Object object = this.regionLock;
        synchronized (object) {
            if (regionsGetter != null) {
                GameLinkedList<T> regionList = regionsGetter.getList(regionX, regionY);
                if (this.regionElement != null) {
                    if (this.regionElement.getList() == regionList) {
                        regionList = null;
                    } else {
                        this.regionElement.remove();
                        this.regionElement = null;
                    }
                }
                if (regionList != null) {
                    this.regionElement = regionList.addLast(this.entity);
                }
            } else if (this.regionElement != null) {
                this.regionElement.remove();
                this.regionElement = null;
            }
        }
        return new SingletonPointSet(regionX, regionY);
    }

    public abstract boolean isDisposed();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearElements() {
        Object object = this.regionLock;
        synchronized (object) {
            if (this.regionElement != null) {
                this.regionElement.remove();
                this.regionElement = null;
            }
        }
    }
}

