/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.registries.WorldPresetRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.level.maps.regionSystem.Region;

public class WorldPresetsRegion {
    public static boolean DEBUG_GENERATING_PRESETS = false;
    public static boolean DEBUG_PLACING_PRESETS = false;
    public static final int PRESET_REGION_REGION_BITS = 6;
    public static final int PRESET_REGION_REGION_SIZE = 64;
    public final WorldEntity worldEntity;
    public final int worldPresetRegionX;
    public final int worldPresetRegionY;
    public final int startLevelRegionX;
    public final int startLevelRegionY;
    public final int startTileX;
    public final int startTileY;
    public final int endTileX;
    public final int endTileY;
    public final int tileHeight;
    public final int tileWidth;
    protected HashMap<LevelIdentifier, Future<LevelPresetsRegion>> levelRegions = new HashMap();
    protected LevelPresetsRegion lastLevelPresetsRegion = null;

    public WorldPresetsRegion(WorldEntity worldEntity, int worldPresetRegionX, int worldPresetRegionY) {
        this.worldEntity = worldEntity;
        this.worldPresetRegionX = worldPresetRegionX;
        this.worldPresetRegionY = worldPresetRegionY;
        this.startLevelRegionX = worldPresetRegionX * 64;
        this.startLevelRegionY = worldPresetRegionY * 64;
        this.startTileX = worldPresetRegionX * 64 * 16;
        this.startTileY = worldPresetRegionY * 64 * 16;
        this.tileWidth = 1024;
        this.tileHeight = 1024;
        this.endTileX = this.startTileX + this.tileWidth - 1;
        this.endTileY = this.startTileY + this.tileHeight - 1;
    }

    public static int getWorldPresetsRegionFromLevelRegion(int region) {
        return GameMath.divideByPowerOf2RoundedDown(region, 6);
    }

    public boolean isRegionWithinBounds(int regionX, int regionY) {
        return regionX >= this.startLevelRegionX && regionX < this.startLevelRegionX + 64 && regionY >= this.startLevelRegionY && regionY < this.startLevelRegionY + 64;
    }

    public synchronized int getLoadedLevelRegionsCount(LevelIdentifier identifier) {
        if (identifier == null) {
            return this.levelRegions.size();
        }
        return this.levelRegions.get(identifier) == null ? 0 : 1;
    }

    public synchronized void tickUnloadBuffer() {
        LinkedList<LevelIdentifier> toRemove = new LinkedList<LevelIdentifier>();
        for (Map.Entry<LevelIdentifier, Future<LevelPresetsRegion>> entry : this.levelRegions.entrySet()) {
            Future<LevelPresetsRegion> future = entry.getValue();
            if (!future.isDone()) continue;
            try {
                LevelPresetsRegion presetsRegion = future.get();
                ++presetsRegion.unloadBuffer;
                if (presetsRegion.unloadBuffer <= 20 * Math.max(2, Settings.unloadLevelsCooldown)) continue;
                toRemove.add(entry.getKey());
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        for (LevelIdentifier levelIdentifier : toRemove) {
            System.out.println("Unloading level presets region " + levelIdentifier + " (" + this.startLevelRegionX + "x" + this.startLevelRegionY + ")");
            this.levelRegions.remove(levelIdentifier);
            if (this.lastLevelPresetsRegion == null || !this.lastLevelPresetsRegion.identifier.equals(levelIdentifier)) continue;
            this.lastLevelPresetsRegion = null;
        }
    }

    public synchronized void refreshRegionUnloadBuffer(LevelIdentifier identifier) {
        Future<LevelPresetsRegion> future = this.getLevelRegionsFuture(identifier, 0);
        if (future.isDone()) {
            try {
                future.get().unloadBuffer = 0;
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected Future<LevelPresetsRegion> getLevelRegionsFuture(LevelIdentifier identifier, int customSeed) {
        Future<LevelPresetsRegion> region = this.levelRegions.get(identifier);
        if (region == null) {
            boolean debugTimer = DEBUG_GENERATING_PRESETS;
            PerformanceTimerManager timer = debugTimer ? new PerformanceTimerManager(false) : (this.worldEntity.isServer() ? this.worldEntity.getServer().tickManager().getChild() : (this.worldEntity.isClient() ? this.worldEntity.getClient().tickManager().getChild() : null));
            LevelPresetsRegion existingRegion = this.levelRegions.values().stream().findFirst().map(f -> {
                if (f.isDone()) {
                    try {
                        LevelPresetsRegion presetsRegion = (LevelPresetsRegion)f.get();
                        presetsRegion.unloadBuffer = 0;
                        return presetsRegion;
                    }
                    catch (InterruptedException | ExecutionException e) {
                        return null;
                    }
                }
                return null;
            }).orElse(null);
            Future<LevelPresetsRegion> future = this.worldEntity.executor().submit(() -> {
                System.out.println("Starting to load level presets region " + identifier + " (" + this.startLevelRegionX + "x" + this.startLevelRegionY + ")");
                LevelPresetsRegion nextRegion = new LevelPresetsRegion(this, identifier, existingRegion, timer);
                nextRegion.loadGeneratedPresetsFile();
                Performance.record(timer, "initPresets", () -> WorldPresetRegistry.initRegion(nextRegion, customSeed, timer));
                this.worldEntity.removePresetsNearbySpawn(nextRegion);
                if (debugTimer) {
                    System.out.println("PERFORMANCE RESULTS FOR GENERATING WORLD PRESET " + nextRegion.identifier + " (" + this.startLevelRegionX + "x" + this.startLevelRegionY + "):");
                    PerformanceTimerUtils.printPerformanceTimer(timer.getCurrentRootPerformanceTimer());
                }
                return nextRegion;
            });
            this.levelRegions.put(identifier, future);
            region = future;
        }
        return region;
    }

    public synchronized boolean isLevelRegionLoadingOrLoaded(LevelIdentifier identifier) {
        return this.levelRegions.get(identifier) != null;
    }

    public synchronized LevelPresetsRegion getLevelRegions(LevelIdentifier identifier, int customSeed) {
        if (this.lastLevelPresetsRegion != null && this.lastLevelPresetsRegion.identifier.equals(identifier)) {
            return this.lastLevelPresetsRegion;
        }
        Future<LevelPresetsRegion> future = this.getLevelRegionsFuture(identifier, customSeed);
        try {
            LevelPresetsRegion region = future.get();
            region.unloadBuffer = 0;
            this.lastLevelPresetsRegion = region;
            return region;
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void saveGeneratedPresetsFile(LevelIdentifier identifier) {
        Future<LevelPresetsRegion> presetsRegionFuture = this.levelRegions.get(identifier);
        if (presetsRegionFuture != null) {
            try {
                presetsRegionFuture.get().saveGeneratedPresetsFile();
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int startGenerateRegion(LevelIdentifier identifier, Region region, int customSeed) {
        return this.getLevelRegions(identifier, customSeed).startGenerateRegion(region);
    }

    public void runGenerateRegion(LevelIdentifier identifier, int generationUniqueID, Region region, int customSeed) {
        this.getLevelRegions(identifier, customSeed).runGenerateRegion(generationUniqueID, region, customSeed);
    }
}

