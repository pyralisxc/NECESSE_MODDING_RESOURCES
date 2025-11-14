/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceWrapper;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.light.SourcedLightModifier;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;

public class InsideLightUpdater {
    public static float FALLOFF_MODIFIER = 3.0f;
    private final LightManager manager;
    private final Level level;
    protected final int startTileX;
    protected final int startTileY;
    protected final int endTileX;
    protected final int endTileY;
    protected final RegionBoundsExecutor regions;

    public InsideLightUpdater(LightManager manager, int startTileX, int startTileY, int endTileX, int endTileY) {
        if (manager.level.isCave) {
            throw new IllegalArgumentException("InsideLightUpdater cannot be used in caves");
        }
        this.manager = manager;
        this.level = manager.level;
        this.startTileX = this.level.limitTileXToBounds(startTileX);
        this.startTileY = this.level.limitTileYToBounds(startTileY);
        this.endTileX = this.level.limitTileXToBounds(endTileX);
        this.endTileY = this.level.limitTileYToBounds(endTileY);
        this.regions = new RegionBoundsExecutor(this.level.regionManager, this.level.limitTileXToBounds(startTileX - 25), this.level.limitTileYToBounds(startTileY - 25), this.level.limitTileXToBounds(endTileX + 25), this.level.limitTileYToBounds(endTileY + 25), false);
    }

    public LinkedList<SourcedLightModifier> getSources(int tileX, int tileY) {
        return this.regions.getOnTile(tileX, tileY, (region, regionTileX, regionTileY) -> region.lightLayer.getInsideSourcesByRegion(regionTileX, regionTileY), null);
    }

    public LinkedList<SourcedLightModifier> getOrCreateSources(int tileX, int tileY) {
        return this.regions.getOnTile(tileX, tileY, (region, regionTileX, regionTileY) -> region.lightLayer.getOrCreateInsideSourcesByRegion(regionTileX, regionTileY), null);
    }

    public void clearSources(int tileX, int tileY) {
        this.regions.runOnTile(tileX, tileY, (region, regionTileX, regionTileY) -> region.lightLayer.clearInsideSourcesByRegion(regionTileX, regionTileY));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runUpdate() {
        boolean recordConstant = this.level.debugLoadingPerformance != null;
        PerformanceTimerManager tickManager = this.level.debugLoadingPerformance != null ? this.level.debugLoadingPerformance : this.level.tickManager();
        PerformanceWrapper timer = Performance.wrapTimer(tickManager, "updateInsideLights", recordConstant);
        try {
            for (int tileX = this.startTileX; tileX <= this.endTileX; ++tileX) {
                for (int tileY = this.startTileY; tileY <= this.endTileY; ++tileY) {
                    LinkedList<SourcedLightModifier> sources = this.getSources(tileX, tileY);
                    if (sources != null) {
                        for (SourcedLightModifier source : sources) {
                            if (source.sourceX != tileX || source.sourceY != tileY) continue;
                            this.clearSource(tileX, tileY);
                            break;
                        }
                    }
                    if (!this.level.getObject(tileX, tileY).allowsAmbientLightPassThrough(this.level, tileX, tileY)) continue;
                    this.addSource(tileX, tileY);
                }
            }
        }
        finally {
            timer.end();
        }
    }

    private void addSource(int tileX, int tileY) {
        LinkedList<Point> openTiles = new LinkedList<Point>();
        openTiles.add(new Point(tileX, tileY));
        HashMap<Long, Integer> currentValues = new HashMap<Long, Integer>();
        currentValues.put(GameMath.getUniqueLongKey(tileX, tileY), 150);
        while (!openTiles.isEmpty()) {
            Point current = (Point)openTiles.removeFirst();
            long currentKey = GameMath.getUniqueLongKey(current.x, current.y);
            int currentValue = (Integer)currentValues.get(currentKey);
            this.addNewSourceOpenTile(currentValue, current.x + 1, current.y, openTiles, currentValues);
            this.addNewSourceOpenTile(currentValue, current.x - 1, current.y, openTiles, currentValues);
            this.addNewSourceOpenTile(currentValue, current.x, current.y + 1, openTiles, currentValues);
            this.addNewSourceOpenTile(currentValue, current.x, current.y - 1, openTiles, currentValues);
        }
        for (Map.Entry entry : currentValues.entrySet()) {
            long key = (Long)entry.getKey();
            int entryTileX = GameMath.getXFromUniqueLongKey(key);
            int entryTileY = GameMath.getYFromUniqueLongKey(key);
            LinkedList<SourcedLightModifier> sources = this.getOrCreateSources(entryTileX, entryTileY);
            sources.add(new SourcedLightModifier(tileX, tileY, (Integer)entry.getValue()));
        }
    }

    private boolean addNewSourceOpenTile(int currentValue, int tileX, int tileY, LinkedList<Point> openTiles, HashMap<Long, Integer> currentValues) {
        if (!this.regions.isInsideBounds(tileX, tileY)) {
            return false;
        }
        if (!this.regions.isTileLoaded(tileX, tileY)) {
            return false;
        }
        long nextKey = GameMath.getUniqueLongKey(tileX, tileY);
        GameObject nextObject = this.level.getObject(tileX, tileY);
        int lightMod = (int)((float)nextObject.getLightLevelMod(this.level, tileX, tileY) * FALLOFF_MODIFIER);
        int nextValue = Math.max(0, currentValue - lightMod);
        int lastValue = currentValues.getOrDefault(nextKey, 0);
        if (lastValue >= currentValue) {
            return false;
        }
        currentValues.put(nextKey, nextValue);
        openTiles.add(new Point(tileX, tileY));
        return true;
    }

    private void clearSource(int tileX, int tileY) {
        LinkedList<Point> openTiles = new LinkedList<Point>();
        openTiles.add(new Point(tileX, tileY));
        PointHashSet closedTiles = new PointHashSet();
        closedTiles.add(tileX, tileY);
        while (!openTiles.isEmpty()) {
            Point current = (Point)openTiles.removeFirst();
            LinkedList<SourcedLightModifier> sources = this.getSources(current.x, current.y);
            if (sources == null) continue;
            ListIterator li = sources.listIterator();
            while (li.hasNext()) {
                SourcedLightModifier source = (SourcedLightModifier)li.next();
                if (source.sourceX != tileX || source.sourceY != tileY) continue;
                li.remove();
                this.addClearSourcesOpenTile(current.x + 1, current.y, openTiles, closedTiles);
                this.addClearSourcesOpenTile(current.x - 1, current.y, openTiles, closedTiles);
                this.addClearSourcesOpenTile(current.x, current.y + 1, openTiles, closedTiles);
                this.addClearSourcesOpenTile(current.x, current.y - 1, openTiles, closedTiles);
                break;
            }
            if (!sources.isEmpty()) continue;
            this.clearSources(current.x, current.y);
        }
    }

    private boolean addClearSourcesOpenTile(int tileX, int tileY, LinkedList<Point> openTiles, PointHashSet closedTiles) {
        if (!this.regions.isInsideBounds(tileX, tileY)) {
            return false;
        }
        if (!this.regions.isTileLoaded(tileX, tileY)) {
            return false;
        }
        if (closedTiles.contains(tileX, tileY)) {
            return false;
        }
        openTiles.add(new Point(tileX, tileY));
        closedTiles.add(tileX, tileY);
        return true;
    }
}

