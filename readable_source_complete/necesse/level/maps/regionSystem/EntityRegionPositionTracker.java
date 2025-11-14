/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.SingletonPointSet;
import necesse.entity.Entity;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionTracker;

public class EntityRegionPositionTracker<T extends Entity>
implements RegionTracker<T> {
    protected final T entity;
    private final Object regionLock = new Object();
    protected GameLinkedList.Element regionElement;
    protected GameLinkedList.Element saveToRegionElement;
    protected Point saveToRegionPos;

    public EntityRegionPositionTracker(T entity) {
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
        Level level = ((Entity)this.entity).getLevel();
        if (level == null) {
            Object object = this.regionLock;
            synchronized (object) {
                if (this.regionElement != null) {
                    this.regionElement.remove();
                    this.regionElement = null;
                }
                if (this.saveToRegionElement != null) {
                    this.saveToRegionElement.remove();
                    this.saveToRegionElement = null;
                    this.saveToRegionPos = null;
                }
            }
            return null;
        }
        int regionX = level.regionManager.getRegionCoordByTile(((Entity)this.entity).getTileX());
        int regionY = level.regionManager.getRegionCoordByTile(((Entity)this.entity).getTileY());
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
            if (saveToRegionGetter != null && this.shouldTrackSaveToRegion()) {
                GameLinkedList<T> lastLoadedList = saveToRegionGetter.getList(regionX, regionY);
                if (this.saveToRegionElement != null) {
                    if (this.saveToRegionElement.getList() == lastLoadedList) {
                        lastLoadedList = null;
                    } else {
                        this.saveToRegionElement.remove();
                        this.saveToRegionElement = null;
                        this.saveToRegionPos = null;
                    }
                }
                if (lastLoadedList != null) {
                    this.saveToRegionElement = lastLoadedList.addLast(this.entity);
                    this.saveToRegionPos = new Point(regionX, regionY);
                }
            } else if (this.saveToRegionElement != null) {
                this.saveToRegionElement.remove();
                this.saveToRegionElement = null;
                this.saveToRegionPos = null;
            }
        }
        return new SingletonPointSet(regionX, regionY);
    }

    public boolean isDisposed() {
        return ((Entity)this.entity).removed();
    }

    public boolean shouldTrackSaveToRegion() {
        return ((Entity)this.entity).shouldRemoveOnRegionUnload();
    }

    public Point getSaveToRegionPos() {
        return this.saveToRegionPos;
    }

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
            if (this.saveToRegionElement != null) {
                this.saveToRegionElement.remove();
                this.saveToRegionElement = null;
                this.saveToRegionPos = null;
            }
        }
    }
}

