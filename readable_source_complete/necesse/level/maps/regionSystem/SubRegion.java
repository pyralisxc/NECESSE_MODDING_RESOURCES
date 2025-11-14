/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SubRegionDoorChangedFunction;
import necesse.level.maps.regionSystem.SubRegionEventListener;

public class SubRegion {
    public final Region region;
    private GameLinkedList.Element listElement;
    private final HashSet<SubRegion> adjacentRegions = new HashSet();
    private final HashSet<Integer> regionTiles = new HashSet();
    private long totalRegionTileX;
    private long totalRegionTileY;
    private int regionID;
    private int roomID;
    private RegionType type;
    private boolean isInvalidated;
    private final GameLinkedList<SubRegionEventListener> listeners = new GameLinkedList();
    public static final Point[] crossTileOffsets = new Point[]{new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)};

    public SubRegion(Region region, RegionType type) {
        this.region = region;
        this.type = type;
        this.regionID = -1;
        this.roomID = -1;
    }

    public SubRegionEventListener addListener(final Runnable onInvalidated, final SubRegionDoorChangedFunction onDoorChanged) {
        if (this.isInvalidated) {
            throw new IllegalStateException("Cannot add listeners on invalidated subregions");
        }
        final AtomicReference<GameLinkedList.Element> element = new AtomicReference<GameLinkedList.Element>();
        SubRegionEventListener h = new SubRegionEventListener(){

            @Override
            public void onInvalidated() {
                if (onInvalidated != null) {
                    onInvalidated.run();
                }
            }

            @Override
            public void onDoorChanged(DoorObject lastDoor, DoorObject newDoor, int tileX, int tileY) {
                onDoorChanged.handle(lastDoor, newDoor, tileX, tileY);
            }

            @Override
            public void submitHandlerInvalidated() {
                GameLinkedList.Element e = (GameLinkedList.Element)element.get();
                if (e != null && !e.isRemoved()) {
                    e.remove();
                }
            }
        };
        element.set(this.listeners.addLast(h));
        return h;
    }

    public boolean isInvalidated() {
        return this.isInvalidated;
    }

    public void invalidate() {
        if (this.isInvalidated) {
            return;
        }
        for (SubRegion adjacentSubRegion : this.adjacentRegions) {
            adjacentSubRegion.adjacentRegions.remove(this);
        }
        this.region.manager.changeRoomSizeCache(this.getRoomID(), -this.size());
        this.isInvalidated = true;
        for (SubRegionEventListener listener : this.listeners.toArray((T1[])new SubRegionEventListener[0])) {
            listener.onInvalidated();
        }
        this.listeners.clear();
    }

    public int getRegionID() {
        return this.regionID;
    }

    public int getRoomID() {
        return this.roomID;
    }

    public RegionType getType() {
        return this.type;
    }

    public int size() {
        return this.regionTiles.size();
    }

    public void addRegionTile(int regionTileX, int regionTileY, SubRegion[][] subRegionTilesArray) {
        this.totalRegionTileX += (long)regionTileX;
        this.totalRegionTileY += (long)regionTileY;
        int uniqueKey = GameMath.getUniqueIntKey(regionTileX, regionTileY);
        this.regionTiles.add(uniqueKey);
        subRegionTilesArray[regionTileX][regionTileY] = this;
    }

    public static SubRegion floodFillSemiRegion(Region region, GameLinkedList<SubRegion> list, int regionTileX, int regionTileY, boolean[][] calculatedCells, SubRegion[][] subRegionTilesArray) {
        RegionType regionType = region.objectLayer.getObjectByRegion(0, regionTileX, regionTileY).getRegionType();
        SubRegion sr = new SubRegion(region, regionType);
        sr.listElement = list.addLast(sr);
        calculatedCells[regionTileX][regionTileY] = true;
        sr.addRegionTile(regionTileX, regionTileY, subRegionTilesArray);
        if (!regionType.isDoor) {
            LinkedList<Point> openTiles = new LinkedList<Point>();
            openTiles.add(new Point(regionTileX, regionTileY));
            while (!openTiles.isEmpty()) {
                Point current = (Point)openTiles.removeFirst();
                for (Point offset : crossTileOffsets) {
                    GameObject object;
                    int nextRegionTileX = current.x + offset.x;
                    int nextRegionTileY = current.y + offset.y;
                    if (nextRegionTileX < 0 || nextRegionTileY < 0 || nextRegionTileX >= region.tileWidth || nextRegionTileY >= region.tileHeight || calculatedCells[nextRegionTileX][nextRegionTileY] || (object = region.objectLayer.getObjectByRegion(0, nextRegionTileX, nextRegionTileY)).getRegionType() != regionType) continue;
                    sr.addRegionTile(nextRegionTileX, nextRegionTileY, subRegionTilesArray);
                    calculatedCells[nextRegionTileX][nextRegionTileY] = true;
                    openTiles.add(new Point(nextRegionTileX, nextRegionTileY));
                }
            }
        }
        return sr;
    }

    public static void forceUpdate(Level level, int tileX, int tileY) {
        SubRegion otherSubRegion = level.regionManager.getSubRegionByTile(tileX, tileY);
        if (otherSubRegion != null) {
            otherSubRegion.forceUpdateRegionAndRoomID();
        }
    }

    public void findAdjacentSemiRegions() {
        for (SubRegion subRegion : this.adjacentRegions) {
            subRegion.adjacentRegions.remove(this);
        }
        this.adjacentRegions.clear();
        Iterator<Object> iterator = this.regionTiles.iterator();
        while (iterator.hasNext()) {
            int regionTileKey = (Integer)iterator.next();
            int regionTileX = GameMath.getXFromUniqueIntKey(regionTileKey);
            int regionTileY = GameMath.getYFromUniqueIntKey(regionTileKey);
            for (Point offset : crossTileOffsets) {
                int nextRegionTileY;
                int nextRegionTileX;
                SubRegion nextSubRegion;
                int nextTileY;
                int nextTileX;
                Region nextRegion;
                if (this.regionTiles.contains(GameMath.getUniqueIntKey(regionTileX + offset.x, regionTileY + offset.y)) || (nextRegion = this.region.getRegionByTile(nextTileX = regionTileX + offset.x + this.region.tileXOffset, nextTileY = regionTileY + offset.y + this.region.tileYOffset, false)) == null || (nextSubRegion = nextRegion.subRegionData.subRegionTilesArray[nextRegionTileX = nextTileX - nextRegion.tileXOffset][nextRegionTileY = nextTileY - nextRegion.tileYOffset]) == null) continue;
                nextSubRegion.adjacentRegions.add(this);
                this.adjacentRegions.add(nextSubRegion);
            }
        }
    }

    protected void setAndExpandRegionID(int regionID) {
        this.regionID = regionID;
        for (SubRegion subRegion : this.adjacentRegions) {
            if (subRegion.getType() != this.getType() || subRegion.regionID == regionID) continue;
            subRegion.setAndExpandRegionID(regionID);
        }
    }

    protected void forceUpdateRegionAndRoomID() {
        this.regionID = -1;
        this.roomID = -1;
        GameLinkedList<SubRegion> list = new GameLinkedList<SubRegion>();
        list.add(this);
        this.region.subRegionData.updateRegionIDs(list);
        list.add(this);
        this.region.subRegionData.updateRoomIDs(list);
    }

    protected void setAndExpandRoomID(int roomID) {
        if (this.roomID != -1) {
            this.region.manager.changeRoomSizeCache(this.roomID, -this.size());
        }
        this.roomID = roomID;
        this.region.manager.changeRoomSizeCache(roomID, this.size());
        for (SubRegion subRegion : this.adjacentRegions) {
            if (subRegion.getType().roomInt != this.getType().roomInt || subRegion.roomID == roomID) continue;
            subRegion.setAndExpandRoomID(roomID);
        }
    }

    public Iterable<SubRegion> getAdjacentRegions() {
        return this.adjacentRegions;
    }

    public Iterable<SubRegion> getConnectedRegionsOfSameType() {
        return () -> new Iterator<SubRegion>(){
            private final HashSet<SubRegion> visitedRegions = new HashSet();
            private final HashSet<SubRegion> regionsToVisit = new HashSet();
            {
                this.visitedRegions.add(SubRegion.this);
                for (SubRegion adjacentRegion : SubRegion.this.adjacentRegions) {
                    if (adjacentRegion.getType() != SubRegion.this.type) continue;
                    this.regionsToVisit.add(adjacentRegion);
                }
            }

            @Override
            public boolean hasNext() {
                return !this.regionsToVisit.isEmpty();
            }

            @Override
            public SubRegion next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more adjacent regions to visit");
                }
                SubRegion currentRegion = this.regionsToVisit.iterator().next();
                this.regionsToVisit.remove(currentRegion);
                this.visitedRegions.add(currentRegion);
                for (SubRegion adjacentRegion : currentRegion.adjacentRegions) {
                    if (adjacentRegion.getType() != SubRegion.this.type || this.visitedRegions.contains(adjacentRegion)) continue;
                    this.regionsToVisit.add(adjacentRegion);
                }
                return currentRegion;
            }
        };
    }

    public Iterable<SubRegion> getConnectedRegionsOfSameRoomInt() {
        return () -> new Iterator<SubRegion>(){
            private final HashSet<SubRegion> visitedRegions = new HashSet();
            private final HashSet<SubRegion> regionsToVisit = new HashSet();
            {
                this.visitedRegions.add(SubRegion.this);
                for (SubRegion adjacentRegion : SubRegion.this.adjacentRegions) {
                    if (adjacentRegion.getType().roomInt != ((SubRegion)SubRegion.this).type.roomInt) continue;
                    this.regionsToVisit.add(adjacentRegion);
                }
            }

            @Override
            public boolean hasNext() {
                return !this.regionsToVisit.isEmpty();
            }

            @Override
            public SubRegion next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more adjacent regions to visit");
                }
                SubRegion currentRegion = this.regionsToVisit.iterator().next();
                this.regionsToVisit.remove(currentRegion);
                this.visitedRegions.add(currentRegion);
                for (SubRegion adjacentRegion : currentRegion.adjacentRegions) {
                    if (adjacentRegion.getType().roomInt != ((SubRegion)SubRegion.this).type.roomInt || this.visitedRegions.contains(adjacentRegion)) continue;
                    this.regionsToVisit.add(adjacentRegion);
                }
                return currentRegion;
            }
        };
    }

    public Stream<SubRegion> streamAdjacentRegions() {
        return this.adjacentRegions.stream();
    }

    public int getAverageRegionTileX() {
        return (int)(this.totalRegionTileX / (long)this.regionTiles.size());
    }

    public int getAverageRegionTileY() {
        return (int)(this.totalRegionTileY / (long)this.regionTiles.size());
    }

    public Point getAverageRegionTile() {
        return new Point(this.getAverageRegionTileX(), this.getAverageRegionTileY());
    }

    public Point getAverageLevelTile() {
        return this.getLevelTileFromRegionTile(this.getAverageRegionTile());
    }

    public Iterable<Point> getRegionTiles() {
        return GameUtils.mapIterable(this.regionTiles.iterator(), key -> {
            int regionTileX = GameMath.getXFromUniqueIntKey(key);
            int regionTileY = GameMath.getYFromUniqueIntKey(key);
            return new Point(regionTileX, regionTileY);
        });
    }

    public Iterable<Point> getLevelTiles() {
        return GameUtils.mapIterable(this.regionTiles.iterator(), key -> {
            int regionTileX = GameMath.getXFromUniqueIntKey(key);
            int regionTileY = GameMath.getYFromUniqueIntKey(key);
            return new Point(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
        });
    }

    public Stream<Point> streamRegionTiles() {
        return this.regionTiles.stream().map(key -> {
            int regionTileX = GameMath.getXFromUniqueIntKey(key);
            int regionTileY = GameMath.getYFromUniqueIntKey(key);
            return new Point(regionTileX, regionTileY);
        });
    }

    public Stream<Point> streamLevelTiles() {
        return this.regionTiles.stream().map(key -> {
            int regionTileX = GameMath.getXFromUniqueIntKey(key);
            int regionTileY = GameMath.getYFromUniqueIntKey(key);
            return new Point(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
        });
    }

    public boolean hasRegionTile(int regionTileX, int regionTileY) {
        return this.regionTiles.contains(GameMath.getUniqueIntKey(regionTileX, regionTileY));
    }

    public boolean hasLevelTile(int tileX, int tileY) {
        if (tileX < this.region.tileXOffset || tileY < this.region.tileYOffset) {
            return false;
        }
        int regionTileX = tileX - this.region.tileXOffset;
        int regionTileY = tileY - this.region.tileYOffset;
        if (regionTileX >= this.region.tileWidth || regionTileY >= this.region.tileHeight) {
            return false;
        }
        return this.hasRegionTile(regionTileX, regionTileY);
    }

    public Point getLevelTileFromRegionTile(Point regionTile) {
        return new Point(regionTile.x + this.region.tileXOffset, regionTile.y + this.region.tileYOffset);
    }

    public int getListenersSize() {
        return this.listeners.size();
    }

    public GameLinkedList.Element getListElement() {
        return this.listElement;
    }

    public ConnectedSubRegionsResult getAllConnected(Predicate<SubRegion> filter, int maxSize) {
        HashSet<SubRegion> set = new HashSet<SubRegion>();
        int size = this.addAllConnected(set, filter, maxSize);
        return new ConnectedSubRegionsResult(this, set, size);
    }

    public int addAllConnected(HashSet<SubRegion> set, Predicate<SubRegion> filter, int maxSize) {
        LinkedList<SubRegion> queue = new LinkedList<SubRegion>();
        set.add(this);
        queue.add(this);
        int size = this.size();
        while (!queue.isEmpty()) {
            SubRegion current = (SubRegion)queue.removeFirst();
            for (SubRegion adjacentRegion : current.adjacentRegions) {
                if (set.contains(adjacentRegion) || !filter.test(adjacentRegion)) continue;
                if ((size += adjacentRegion.size()) >= maxSize) {
                    return size;
                }
                set.add(adjacentRegion);
                queue.addLast(adjacentRegion);
            }
        }
        return size;
    }

    public boolean isOutside() {
        return this.region.manager.getRoomSize(this.getRoomID()) > RegionManager.INSIDE_MAX_SIZE;
    }

    public void changeDoorTypeByRegion(RegionType newType, DoorObject lastDoor, DoorObject newDoor, int regionTileX, int regionTileY) {
        if (!this.type.isDoor || !newType.isDoor) {
            throw new IllegalStateException("Cannot change SubRegion type on non doors");
        }
        this.type = newType;
        for (SubRegionEventListener listener : this.listeners.toArray((T1[])new SubRegionEventListener[0])) {
            listener.onDoorChanged(lastDoor, newDoor, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
        }
    }
}

