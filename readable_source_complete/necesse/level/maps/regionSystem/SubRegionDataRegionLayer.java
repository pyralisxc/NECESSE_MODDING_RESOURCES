/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameLinkedList;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SubRegion;
import necesse.level.maps.regionSystem.layers.RegionLayer;

public class SubRegionDataRegionLayer
extends RegionLayer {
    protected GameLinkedList<SubRegion> openRegionIDSubRegions = new GameLinkedList();
    protected GameLinkedList<SubRegion> openRoomIDSubRegions = new GameLinkedList();
    protected GameLinkedList<SubRegion> subRegionsList = new GameLinkedList();
    protected SubRegion[][] subRegionTilesArray;

    public SubRegionDataRegionLayer(Region region) {
        super(region);
        this.subRegionTilesArray = new SubRegion[region.tileWidth][region.tileHeight];
    }

    @Override
    public void init() {
    }

    @Override
    public void onLayerLoaded() {
        this.update();
    }

    @Override
    public void onLoadingComplete() {
        this.update();
    }

    @Override
    public void onLayerUnloaded() {
        this.invalidate();
    }

    public void update() {
        if (!this.region.isLoadingComplete() || !this.level.isLoadingComplete()) {
            return;
        }
        boolean recordConstant = this.level.debugLoadingPerformance != null;
        PerformanceTimerManager tickManager = this.level.debugLoadingPerformance != null ? this.level.debugLoadingPerformance : this.level.tickManager();
        Performance.record(tickManager, "regions", recordConstant, () -> {
            this.invalidate();
            Performance.record(tickManager, "subRegions", recordConstant, this::calculateSubRegions);
            Performance.record(tickManager, "adjacentRegions", recordConstant, this::calculateAdjacentRegions);
            Performance.record(tickManager, "ids", recordConstant, () -> {
                Performance.record(tickManager, "regionIDs", recordConstant, () -> this.updateRegionIDs(this.openRegionIDSubRegions));
                Performance.record(tickManager, "roomIDs", recordConstant, () -> this.updateRoomIDs(this.openRoomIDSubRegions));
            });
        });
    }

    protected void calculateSubRegions() {
        this.invalidate();
        boolean[][] calculatedRegionTiles = new boolean[this.region.tileWidth][this.region.tileHeight];
        this.subRegionsList = new GameLinkedList();
        this.openRegionIDSubRegions = new GameLinkedList();
        this.openRoomIDSubRegions = new GameLinkedList();
        this.subRegionTilesArray = new SubRegion[this.region.tileWidth][this.region.tileHeight];
        for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                if (calculatedRegionTiles[regionTileX][regionTileY]) continue;
                SubRegion subRegion = SubRegion.floodFillSemiRegion(this.region, this.subRegionsList, regionTileX, regionTileY, calculatedRegionTiles, this.subRegionTilesArray);
                this.openRegionIDSubRegions.add(subRegion);
                this.openRoomIDSubRegions.add(subRegion);
            }
        }
    }

    protected void calculateAdjacentRegions() {
        for (SubRegion subRegion : this.subRegionsList) {
            subRegion.findAdjacentSemiRegions();
        }
    }

    protected void updateRegionIDs(GameLinkedList<SubRegion> openSubRegions) {
        GameLinkedList<SubRegion> nextOpenSubRegions = new GameLinkedList<SubRegion>();
        boolean foundAnyRegionIDs = false;
        while (!openSubRegions.isEmpty()) {
            SubRegion subRegion = openSubRegions.removeFirst();
            if (subRegion.getRegionID() != -1) continue;
            CollectedSizeResult sizeResult = this.collectRegionSize(subRegion, RegionManager.NEW_REGION_ID_CHECK_SIZE + 1);
            if (sizeResult.totalSize > RegionManager.NEW_REGION_ID_CHECK_SIZE && sizeResult.largestID != -1) {
                subRegion.setAndExpandRegionID(sizeResult.largestID);
                foundAnyRegionIDs = true;
                continue;
            }
            nextOpenSubRegions.add(subRegion);
        }
        if (!foundAnyRegionIDs) {
            for (SubRegion subRegion : nextOpenSubRegions) {
                if (subRegion.getRegionID() != -1) continue;
                int nextRegionID = this.manager.getNextRegionID();
                subRegion.setAndExpandRegionID(nextRegionID);
            }
        } else {
            this.updateRegionIDs(nextOpenSubRegions);
        }
    }

    protected void updateRoomIDs(GameLinkedList<SubRegion> openSubRegions) {
        if (openSubRegions.isEmpty()) {
            return;
        }
        GameLinkedList<SubRegion> nextOpenSubRegions = new GameLinkedList<SubRegion>();
        boolean foundAnyRoomIDs = false;
        while (!openSubRegions.isEmpty()) {
            SubRegion subRegion = openSubRegions.removeFirst();
            if (subRegion.getRoomID() != -1) continue;
            CollectedSizeResult sizeResult = this.collectRoomSize(subRegion, RegionManager.INSIDE_MAX_SIZE + 1);
            if (sizeResult.totalSize > RegionManager.INSIDE_MAX_SIZE && sizeResult.largestID != -1) {
                subRegion.setAndExpandRoomID(sizeResult.largestID);
                foundAnyRoomIDs = true;
                continue;
            }
            nextOpenSubRegions.add(subRegion);
        }
        if (!foundAnyRoomIDs) {
            for (SubRegion subRegion : nextOpenSubRegions) {
                if (subRegion.getRoomID() != -1) continue;
                int nextRoomID = this.manager.getRoomNextID();
                subRegion.setAndExpandRoomID(nextRoomID);
            }
        } else {
            this.updateRoomIDs(nextOpenSubRegions);
        }
    }

    protected CollectedSizeResult collectRegionSize(SubRegion subRegion, int maxSize) {
        int totalSize = subRegion.size();
        if (totalSize >= maxSize) {
            return new CollectedSizeResult(totalSize, totalSize, subRegion.getRegionID());
        }
        HashMap<Integer, Integer> regionSizes = new HashMap<Integer, Integer>();
        regionSizes.put(subRegion.getRegionID(), subRegion.size());
        for (SubRegion adjacentRegion : subRegion.getConnectedRegionsOfSameType()) {
            regionSizes.merge(adjacentRegion.getRegionID(), adjacentRegion.size(), Integer::sum);
            if ((totalSize += adjacentRegion.size()) < maxSize) continue;
            break;
        }
        regionSizes.remove(-1);
        Map.Entry largestRegion = regionSizes.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(new AbstractMap.SimpleEntry<Integer, Integer>(subRegion.getRegionID(), subRegion.size()));
        return new CollectedSizeResult(totalSize, (Integer)largestRegion.getValue(), (Integer)largestRegion.getKey());
    }

    protected CollectedSizeResult collectRoomSize(SubRegion subRegion, int maxSize) {
        int totalSize = subRegion.size();
        if (totalSize >= maxSize) {
            return new CollectedSizeResult(totalSize, totalSize, subRegion.getRoomID());
        }
        HashMap<Integer, Integer> roomSizes = new HashMap<Integer, Integer>();
        roomSizes.put(subRegion.getRoomID(), subRegion.size());
        for (SubRegion adjacentRegion : subRegion.getConnectedRegionsOfSameRoomInt()) {
            roomSizes.merge(adjacentRegion.getRoomID(), adjacentRegion.size(), Integer::sum);
            if ((totalSize += adjacentRegion.size()) < maxSize) continue;
            break;
        }
        roomSizes.remove(-1);
        Map.Entry largestRoom = roomSizes.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(new AbstractMap.SimpleEntry<Integer, Integer>(subRegion.getRoomID(), subRegion.size()));
        return new CollectedSizeResult(totalSize, (Integer)largestRoom.getValue(), (Integer)largestRoom.getKey());
    }

    public void onObjectChanged(int regionTileX, int regionTileY, GameObject oldObject, GameObject newObject) {
        RegionType oldType = oldObject.getRegionType();
        RegionType newType = newObject.getRegionType();
        SubRegion subregion = this.getSubRegionByRegion(regionTileX, regionTileY);
        if (oldType.isDoor && newType.isDoor) {
            if (subregion != null) {
                subregion.changeDoorTypeByRegion(newType, (DoorObject)oldObject, (DoorObject)newObject, regionTileX, regionTileY);
            }
        } else if (oldType != newType) {
            this.update();
            if (regionTileX == 0) {
                SubRegion.forceUpdate(this.level, this.region.tileXOffset - 1, this.region.tileYOffset + regionTileY);
            } else if (regionTileX == this.region.tileWidth - 1) {
                SubRegion.forceUpdate(this.level, this.region.tileXOffset + this.region.tileWidth, this.region.tileYOffset + regionTileY);
            }
            if (regionTileY == 0) {
                SubRegion.forceUpdate(this.level, this.region.tileXOffset + regionTileX, this.region.tileYOffset - 1);
            } else if (regionTileY == this.region.tileHeight - 1) {
                SubRegion.forceUpdate(this.level, this.region.tileXOffset + regionTileX, this.region.tileYOffset + this.region.tileHeight);
            }
        }
    }

    public void invalidate() {
        for (SubRegion subRegion : this.subRegionsList) {
            subRegion.invalidate();
        }
    }

    public SubRegion getSubRegionByRegion(int regionTileX, int regionTileY) {
        return this.subRegionTilesArray[regionTileX][regionTileY];
    }

    public int getRegionIDByRegion(int regionTileX, int regionTileY) {
        SubRegion subRegion = this.getSubRegionByRegion(regionTileX, regionTileY);
        if (subRegion == null) {
            return 0;
        }
        return subRegion.getRegionID();
    }

    public int getRoomIDByRegion(int regionTileX, int regionTileY) {
        SubRegion subRegion = this.getSubRegionByRegion(regionTileX, regionTileY);
        if (subRegion == null) {
            return 0;
        }
        return subRegion.getRoomID();
    }

    public boolean isOutsideByRegion(int regionTileX, int regionTileY) {
        SubRegion subRegion = this.getSubRegionByRegion(regionTileX, regionTileY);
        if (subRegion == null) {
            return true;
        }
        return subRegion.isOutside();
    }

    public ConnectedSubRegionsResult getTypeConnectedByRegion(int regionTileX, int regionTileY, int maxSize) {
        SubRegion subRegion = this.getSubRegionByRegion(regionTileX, regionTileY);
        if (subRegion == null) {
            return null;
        }
        return subRegion.getAllConnected(sr -> sr.getType() == subRegion.getType(), maxSize);
    }

    public ConnectedSubRegionsResult getRoomConnectedByRegion(int regionTileX, int regionTileY, boolean onlyReturnOpenRoomInt, int maxSize) {
        SubRegion subRegion = this.getSubRegionByRegion(regionTileX, regionTileY);
        if (subRegion == null) {
            return null;
        }
        if (onlyReturnOpenRoomInt && subRegion.getType().roomInt != RegionType.OPEN.roomInt) {
            return null;
        }
        return subRegion.getAllConnected(sr -> sr.getType().roomInt == subRegion.getType().roomInt, maxSize);
    }

    public ConnectedSubRegionsResult getHouseConnectedByRegion(int regionTileX, int regionTileY, int maxSize) {
        SubRegion subRegion = this.getSubRegionByRegion(regionTileX, regionTileY);
        if (subRegion == null) {
            return null;
        }
        if (subRegion.isOutside()) {
            return null;
        }
        if (subRegion.getType() == RegionType.WALL) {
            return null;
        }
        return subRegion.getAllConnected(sr -> !sr.isOutside() && sr.getType() != RegionType.WALL, maxSize);
    }

    protected static class CollectedSizeResult {
        public final int totalSize;
        public final int largestSize;
        public final int largestID;

        public CollectedSizeResult(int totalSize, int largestSize, int largestID) {
            this.totalSize = totalSize;
            this.largestSize = largestSize;
            this.largestID = largestID;
        }
    }
}

