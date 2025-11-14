/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.managers;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;
import necesse.level.maps.regionSystem.layers.BiomeBlendingOptions;
import necesse.level.maps.regionSystem.layers.BiomeBlendingValue;

public class BiomeBlendingManager {
    public static int BLEND_RANGE = 4;
    public static int MAX_VALUE = 100;
    public final Level level;
    protected static Point[] crossPoints = new Point[]{new Point(0, 1), new Point(1, 0), new Point(0, -1), new Point(-1, 0)};

    public BiomeBlendingManager(Level level) {
        this.level = level;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BiomeBlendingOptions.BiomeBlendingOption[] getBlendOptions(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return new BiomeBlendingOptions.BiomeBlendingOption[0];
        }
        int regionTileX = tileX - region.tileXOffset;
        int regionTileY = tileY - region.tileYOffset;
        BiomeBlendingOptions blendingOptions = region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY);
        Object object = this.level.entityManager.lock;
        synchronized (object) {
            if (blendingOptions == null) {
                return null;
            }
            return blendingOptions.getBlendingOptions(region, tileX, tileY);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LinkedList<BiomeBlendingValue> getBlendValues(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return new LinkedList<BiomeBlendingValue>();
        }
        int regionTileX = tileX - region.tileXOffset;
        int regionTileY = tileY - region.tileYOffset;
        BiomeBlendingOptions blendingOptions = region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY);
        Object object = this.level.entityManager.lock;
        synchronized (object) {
            return blendingOptions.getBlendingValues();
        }
    }

    public void updateBlends(int tileX, int tileY) {
        this.updateBlends(tileX - 1, tileY - 1, tileX + 1, tileY + 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateBlends(int startTileX, int startTileY, int endTileX, int endTileY) {
        int regionTileY;
        int tileY;
        BiomeBlendingOptions blendingOptions;
        int regionTileY2;
        int regionTileX;
        int tileX;
        if (this.level.isServer()) {
            return;
        }
        startTileX = this.level.limitTileXToBounds(startTileX);
        startTileY = this.level.limitTileYToBounds(startTileY);
        endTileX = this.level.limitTileXToBounds(endTileX);
        endTileY = this.level.limitTileYToBounds(endTileY);
        RegionBoundsExecutor regions = new RegionBoundsExecutor(this.level.regionManager, this.level.limitTileXToBounds(startTileX) - BLEND_RANGE - 1, this.level.limitTileYToBounds(startTileY) - BLEND_RANGE - 1, this.level.limitTileXToBounds(endTileX) + BLEND_RANGE + 1, this.level.limitTileYToBounds(endTileY) + BLEND_RANGE + 1, false);
        PointHashMap<Integer> sourceRemoves = new PointHashMap<Integer>();
        HashMap<Integer, BiomeBlendingCompute> blendingComputes = new HashMap<Integer, BiomeBlendingCompute>();
        for (tileX = startTileX; tileX <= endTileX; ++tileX) {
            for (int tileY2 = startTileY; tileY2 <= endTileY; ++tileY2) {
                Region region = (Region)regions.getRegionByTile(tileX, tileY2);
                if (region == null) continue;
                regionTileX = tileX - region.tileXOffset;
                regionTileY2 = tileY2 - region.tileYOffset;
                int biomeID = region.biomeLayer.getBiomeIDByRegion(regionTileX, regionTileY2);
                blendingOptions = region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY2);
                Iterator<Map.Entry<Integer, BiomeBlendingValue>> iterator = this.level.entityManager.lock;
                synchronized (iterator) {
                    LinkedList<Integer> biomeIDsToRemove = new LinkedList<Integer>();
                    for (Map.Entry<Integer, BiomeBlendingValue> entry : blendingOptions.getBlendingSources()) {
                        int sourceBiomeID = entry.getKey();
                        BiomeBlendingValue blending = entry.getValue();
                        if (sourceBiomeID == biomeID || blending.sourceTileX == tileX && blending.sourceTileY == tileY2) {
                            biomeIDsToRemove.add(sourceBiomeID);
                            sourceRemoves.put(tileX, tileY2, sourceBiomeID);
                            continue;
                        }
                        if (blending.sourceTileX >= startTileX && blending.sourceTileY >= startTileY && blending.sourceTileX <= endTileX && blending.sourceTileY <= endTileY || entry.getValue().value <= 0) continue;
                        BiomeBlendingCompute sourceCompute = blendingComputes.compute(sourceBiomeID, (id, last) -> {
                            if (last == null) {
                                return new BiomeBlendingCompute(this, regions, (int)id);
                            }
                            return last;
                        });
                        sourceCompute.openTiles.add(new SourcedPoint(blending.sourceTileX, blending.sourceTileY, tileX, tileY2));
                    }
                    Iterator<Map.Entry<Integer, BiomeBlendingValue>> iterator2 = biomeIDsToRemove.iterator();
                    while (iterator2.hasNext()) {
                        int sourceBiomeID = (Integer)((Object)iterator2.next());
                        blendingOptions.removeBiome(sourceBiomeID);
                    }
                }
                BiomeBlendingCompute compute = blendingComputes.compute(biomeID, (id, last) -> {
                    if (last == null) {
                        return new BiomeBlendingCompute(this, regions, (int)id);
                    }
                    return last;
                });
                compute.openTiles.add(new SourcedPoint(tileX, tileY2, tileX, tileY2));
                blendingOptions.setBlendValue(biomeID, tileX, tileY2, MAX_VALUE);
            }
        }
        if (this.level.isTileXWithinBounds(startTileX - 1)) {
            --startTileX;
            for (tileY = startTileY; tileY <= endTileY; ++tileY) {
                Region region = (Region)regions.getRegionByTile(startTileX, tileY);
                if (region == null) continue;
                int regionTileX2 = startTileX - region.tileXOffset;
                regionTileY = tileY - region.tileYOffset;
                region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX2, regionTileY).markUpdateBlendOptions();
            }
        }
        if (this.level.isTileXWithinBounds(endTileX + 1)) {
            ++endTileX;
            for (tileY = startTileY; tileY <= endTileY; ++tileY) {
                Region region = (Region)regions.getRegionByTile(endTileX, tileY);
                if (region == null) continue;
                int regionTileX3 = endTileX - region.tileXOffset;
                regionTileY = tileY - region.tileYOffset;
                region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX3, regionTileY).markUpdateBlendOptions();
            }
        }
        if (this.level.isTileYWithinBounds(startTileY - 1)) {
            for (tileX = startTileX; tileX <= endTileX; ++tileX) {
                int tileY3 = startTileY - 1;
                Region region = (Region)regions.getRegionByTile(tileX, tileY3);
                if (region == null) continue;
                regionTileX = tileX - region.tileXOffset;
                regionTileY2 = tileY3 - region.tileYOffset;
                region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY2).markUpdateBlendOptions();
            }
        }
        if (this.level.isTileYWithinBounds(endTileY + 1)) {
            for (tileX = startTileX; tileX <= endTileX; ++tileX) {
                int tileY4 = endTileY + 1;
                Region region = (Region)regions.getRegionByTile(tileX, tileY4);
                if (region == null) continue;
                regionTileX = tileX - region.tileXOffset;
                regionTileY2 = tileY4 - region.tileYOffset;
                region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY2).markUpdateBlendOptions();
            }
        }
        PointHashSet nextChecks = new PointHashSet();
        for (Point tile : sourceRemoves.getKeys()) {
            this.removeSource(regions, (Integer)sourceRemoves.get(tile.x, tile.y), tile.x, tile.y, nextChecks);
        }
        for (Point tile : nextChecks) {
            Region region = (Region)regions.getRegionByTile(tile.x, tile.y);
            if (region == null) continue;
            int regionTileX4 = tile.x - region.tileXOffset;
            int regionTileY3 = tile.y - region.tileYOffset;
            blendingOptions = region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX4, regionTileY3);
            for (Map.Entry<Integer, BiomeBlendingValue> entry : blendingOptions.getBlendingSources()) {
                int biomeID = entry.getKey();
                BiomeBlendingValue value = entry.getValue();
                BiomeBlendingCompute compute = blendingComputes.compute(biomeID, (id, last) -> {
                    if (last == null) {
                        return new BiomeBlendingCompute(this, regions, (int)id);
                    }
                    return last;
                });
                if (value.value <= 0) continue;
                compute.openTiles.add(new SourcedPoint(value.sourceTileX, value.sourceTileY, tile.x, tile.y));
            }
        }
        for (BiomeBlendingCompute compute : blendingComputes.values()) {
            compute.run();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeSource(RegionBoundsExecutor regions, int biomeID, int sourceTileX, int sourceTileY, PointHashSet nextChecks) {
        LinkedList<Point> open = new LinkedList<Point>();
        PointHashSet closed = new PointHashSet();
        open.add(new Point(sourceTileX, sourceTileY));
        closed.add(sourceTileX, sourceTileY);
        while (!open.isEmpty()) {
            Point current = (Point)open.removeFirst();
            for (Point delta : crossPoints) {
                Region region;
                int nextTileX = current.x + delta.x;
                int nextTileY = current.y + delta.y;
                if (!regions.isInsideBounds(nextTileX, nextTileY) || closed.contains(nextTileX, nextTileY) || !this.level.isTileWithinBounds(nextTileX, nextTileY) || (region = (Region)regions.getRegionByTile(nextTileX, nextTileY)) == null) continue;
                int regionTileX = nextTileX - region.tileXOffset;
                int regionTileY = nextTileY - region.tileYOffset;
                BiomeBlendingOptions blendingOptions = region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY);
                Object object = this.level.entityManager.lock;
                synchronized (object) {
                    if (blendingOptions.removeSourceAndReturnBiomeID(biomeID, sourceTileX, sourceTileY) != -1) {
                        open.add(new Point(nextTileX, nextTileY));
                    } else {
                        nextChecks.add(nextTileX, nextTileY);
                    }
                }
                closed.add(nextTileX, nextTileY);
            }
        }
    }

    protected static class BiomeBlendingCompute {
        public final BiomeBlendingManager manager;
        public final RegionBoundsExecutor regions;
        public final int biomeID;
        public final int blendingPriority;
        public LinkedList<SourcedPoint> openTiles = new LinkedList();

        public BiomeBlendingCompute(BiomeBlendingManager manager, RegionBoundsExecutor regions, int biomeID) {
            this.manager = manager;
            this.regions = regions;
            this.biomeID = biomeID;
            this.blendingPriority = BiomeRegistry.getBiome(biomeID).getBiomeBlendingPriority();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            while (!this.openTiles.isEmpty()) {
                SourcedPoint current = this.openTiles.removeFirst();
                for (Point crossPoint : crossPoints) {
                    int regionTileY;
                    int regionTileX;
                    Biome biome;
                    Region region;
                    int nextTileX = current.currentTileX + crossPoint.x;
                    int nextTileY = current.currentTileY + crossPoint.y;
                    if (!this.manager.level.isTileWithinBounds(nextTileX, nextTileY) || !this.regions.isInsideBounds(nextTileX, nextTileY) || (region = (Region)this.regions.getRegionByTile(nextTileX, nextTileY)) == null || (biome = region.biomeLayer.getBiomeByRegion(regionTileX = nextTileX - region.tileXOffset, regionTileY = nextTileY - region.tileYOffset)).getID() == this.biomeID || biome.getBiomeBlendingPriority() == this.blendingPriority) continue;
                    double nextRange = Math.max(GameMath.diagonalMoveDistance(nextTileX, nextTileY, current.sourceTileX, current.sourceTileY) - 1.0, 0.0);
                    boolean shouldOpen = true;
                    if (nextRange >= (double)BLEND_RANGE) {
                        nextRange = BLEND_RANGE;
                        shouldOpen = false;
                    }
                    double nextRangeFloat = nextRange / (double)BLEND_RANGE;
                    int nextValue = (int)((1.0 - nextRangeFloat) * (double)MAX_VALUE);
                    BiomeBlendingOptions blendingOptions = region.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY);
                    Object object = this.manager.level.entityManager.lock;
                    synchronized (object) {
                        BiomeBlendingValue currentValue = blendingOptions.getBlendValue(this.biomeID);
                        if (currentValue != null && currentValue.value >= nextValue) {
                            continue;
                        }
                        blendingOptions.setBlendValue(this.biomeID, current.sourceTileX, current.sourceTileY, nextValue);
                    }
                    if (!shouldOpen) continue;
                    this.openTiles.addLast(new SourcedPoint(current.sourceTileX, current.sourceTileY, nextTileX, nextTileY));
                }
            }
        }
    }

    protected static class SourcedPoint {
        public int sourceTileX;
        public int sourceTileY;
        public int currentTileX;
        public int currentTileY;

        public SourcedPoint(int sourceTileX, int sourceTileY, int currentTileX, int currentTileY) {
            this.sourceTileX = sourceTileX;
            this.sourceTileY = sourceTileY;
            this.currentTileX = currentTileX;
            this.currentTileY = currentTileY;
        }
    }
}

