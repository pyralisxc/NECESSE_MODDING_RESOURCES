/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.Iterator;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.regionSystem.RegionPositionGetter;
import necesse.level.maps.regionSystem.RegionTracker;

public abstract class AllRegionPositionsTracker<T extends RegionPositionGetter>
implements RegionTracker<T> {
    protected final T regionPositionGetter;
    private final Object regionLock = new Object();
    protected PointHashMap<GameLinkedList.Element> regionElements = new PointHashMap();
    protected GameLinkedList.Element saveToRegionElement;
    protected GameLinkedList.Element noRegionElement;

    public AllRegionPositionsTracker(T regionPositionGetter) {
        this.regionPositionGetter = regionPositionGetter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PointSetAbstract<?> updateRegion(RegionTrackerList.RegionListGetter<T> regionsGetter, RegionTrackerList.RegionListGetter<T> saveToRegionGetter, GameLinkedList<? super T> noRegionList) {
        if (this.isDisposed()) {
            return null;
        }
        PointSetAbstract<?> regionPositions = this.regionPositionGetter.getRegionPositions();
        Object object = this.regionLock;
        synchronized (object) {
            if (regionsGetter != null) {
                GameLinkedList.Element last;
                PointHashSet regionElementsToRemove = new PointHashSet(this.regionElements.size());
                for (Point region : this.regionElements.getKeys()) {
                    regionElementsToRemove.add(region.x, region.y);
                }
                for (Point regionPosition : regionPositions) {
                    regionElementsToRemove.remove(regionPosition.x, regionPosition.y);
                    last = this.regionElements.get(regionPosition.x, regionPosition.y);
                    GameLinkedList<T> list = regionsGetter.getList(regionPosition.x, regionPosition.y);
                    if (last != null) {
                        if (last.getList() == list) continue;
                        last.remove();
                    }
                    if (list == null) continue;
                    this.regionElements.put(regionPosition.x, regionPosition.y, list.addLast(this.regionPositionGetter));
                }
                Iterator<Point> iterator = regionElementsToRemove.iterator();
                while (iterator.hasNext()) {
                    Point region;
                    region = iterator.next();
                    last = this.regionElements.remove(region.x, region.y);
                    if (last == null) continue;
                    last.remove();
                }
            } else {
                for (GameLinkedList.Element element : this.regionElements.values()) {
                    element.remove();
                }
                this.regionElements.clear();
            }
            if (saveToRegionGetter != null) {
                Point regionPos = this.getSaveToRegionPos();
                if (regionPos != null) {
                    GameLinkedList<T> lastLoadedList = saveToRegionGetter.getList(regionPos.x, regionPos.y);
                    if (this.saveToRegionElement != null) {
                        if (this.saveToRegionElement.getList() == lastLoadedList) {
                            lastLoadedList = null;
                        } else {
                            this.saveToRegionElement.remove();
                            this.saveToRegionElement = null;
                        }
                    }
                    if (lastLoadedList != null) {
                        this.saveToRegionElement = lastLoadedList.addLast(this.regionPositionGetter);
                    }
                } else if (this.saveToRegionElement != null) {
                    this.saveToRegionElement.remove();
                    this.saveToRegionElement = null;
                }
            } else if (this.saveToRegionElement != null) {
                this.saveToRegionElement.remove();
                this.saveToRegionElement = null;
            }
            if (regionPositions.isEmpty()) {
                if (this.noRegionElement != null) {
                    if (this.noRegionElement.getList() == noRegionList) {
                        noRegionList = null;
                    } else {
                        this.noRegionElement.remove();
                        this.noRegionElement = null;
                    }
                }
                if (noRegionList != null) {
                    this.noRegionElement = noRegionList.addLast(this.regionPositionGetter);
                }
            } else if (this.noRegionElement != null) {
                this.noRegionElement.remove();
                this.noRegionElement = null;
            }
        }
        return regionPositions;
    }

    public abstract boolean isDisposed();

    public abstract Point getSaveToRegionPos();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearElements() {
        Object object = this.regionLock;
        synchronized (object) {
            for (GameLinkedList.Element element : this.regionElements.values()) {
                element.remove();
            }
            this.regionElements.clear();
            if (this.saveToRegionElement != null) {
                this.saveToRegionElement.remove();
                this.saveToRegionElement = null;
            }
        }
    }
}

