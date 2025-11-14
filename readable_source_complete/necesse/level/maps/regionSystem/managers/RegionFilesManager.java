/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.managers;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.world.WorldFile;
import necesse.engine.world.WorldFileSystem;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;

public class RegionFilesManager {
    protected static final int WORLD_FILE_REGION_SIZE_BITS = 6;
    protected static final int WORLD_FILE_REGION_SIZE = 64;
    protected static final int WORLD_FILE_REGION_SIZE_HALF = 32;
    public final RegionManager manager;
    public final Level level;
    protected HashMap<Long, WorldRegionData> loadedCache = new HashMap();
    protected boolean initializedServerLevel;

    public static int getWorldRegionPos(int regionPos) {
        return GameMath.divideByPowerOf2RoundedDown(regionPos - 32, 6);
    }

    public RegionFilesManager(RegionManager manager) {
        this.manager = manager;
        this.level = manager.level;
    }

    public void makeServerLevel() {
        if (this.initializedServerLevel) {
            return;
        }
        this.manager.forEachLoadedRegions(region -> this.getWorldRegion(region.regionX, region.regionY, false));
        this.initializedServerLevel = true;
    }

    public synchronized boolean loadRegion(Region region) {
        Server server = this.level.getServer();
        if (server != null) {
            WorldRegionData cache = this.getWorldRegion(region.regionX, region.regionY, false);
            try {
                LoadData save = cache.getSaveData(region.regionX, region.regionY);
                if (save != null) {
                    boolean recordConstant = this.level.debugLoadingPerformance != null;
                    PerformanceTimerManager tickManager = this.level.debugLoadingPerformance != null ? this.level.debugLoadingPerformance : this.level.tickManager();
                    region.loadSaveData(save, tickManager, recordConstant);
                    return true;
                }
            }
            catch (Exception e) {
                System.err.println("Could not load region from file at " + region.regionX + "x" + region.regionY);
                e.printStackTrace();
            }
            server.printMigrationIfShould();
        }
        return false;
    }

    public synchronized void onUnloaded(Region region) {
        if (!this.level.isServer()) {
            return;
        }
        WorldRegionData cache = this.getWorldRegion(region.regionX, region.regionY, true);
        cache.removeRegionAndGetNewSaveData(region.regionX, region.regionY, region::addSaveData);
        cache.loadedRegions.remove(GameMath.getUniqueLongKey(region.regionX, region.regionY));
        if (cache.loadedRegions.isEmpty()) {
            System.out.println("Unloading " + this.level.getIdentifier() + " world region " + cache.worldRegionX + "x" + cache.worldRegionY + " from memory to file system");
            cache.saveToFile(this.level);
            this.loadedCache.remove(GameMath.getUniqueLongKey(cache.worldRegionX, cache.worldRegionY));
        }
    }

    public synchronized void updateSaveFile(Region region) {
        if (!this.level.isServer()) {
            return;
        }
        WorldRegionData cache = this.getWorldRegion(region.regionX, region.regionY, true);
        cache.removeRegionAndGetNewSaveData(region.regionX, region.regionY, region::addSaveData);
    }

    protected WorldRegionData getWorldRegion(int regionX, int regionY, boolean expectedExists) {
        return this.getWorldRegion(regionX, regionY, expectedExists, true);
    }

    protected WorldRegionData getWorldRegion(int regionX, int regionY, boolean expectedExists, boolean loadRegion) {
        int worldRegionY;
        int worldRegionX = RegionFilesManager.getWorldRegionPos(regionX);
        long worldUniqueKey = GameMath.getUniqueLongKey(worldRegionX, worldRegionY = RegionFilesManager.getWorldRegionPos(regionY));
        WorldRegionData cache = this.loadedCache.get(worldUniqueKey);
        if (cache == null) {
            if (expectedExists) {
                System.err.println("Error trying to find world cache for " + this.level.getIdentifier() + " region " + regionX + "x" + regionY + ", World region: " + worldRegionX + "x" + worldRegionY + ", " + this.level.getHostString());
            }
            if (this.level.isServer()) {
                Server server = this.level.getServer();
                WorldFileSystem fileSystem = server.world.fileSystem;
                if (fileSystem.worldRegionFileExists(this.level.getIdentifier(), worldRegionX, worldRegionY)) {
                    LoadData save;
                    WorldFile file = fileSystem.getWorldRegionFile(this.level.getIdentifier(), worldRegionX, worldRegionY);
                    if (expectedExists) {
                        WorldFile backupFile = fileSystem.getBackupWorldRegionFile(this.level.getIdentifier(), worldRegionX, worldRegionY);
                        System.err.println("Trying to make a backup of the world file to " + backupFile.getFileName());
                        try {
                            file.copyTo(backupFile, new CopyOption[0]);
                        }
                        catch (IOException e) {
                            System.err.println("Error making backup:");
                            e.printStackTrace();
                        }
                    }
                    try {
                        save = new LoadData(file);
                    }
                    catch (Exception e) {
                        save = new SaveData("").toLoadData();
                        System.err.println("Could not load " + this.level.getIdentifier() + " region from file at " + regionX + "x" + regionY + ", World region: " + worldRegionX + "x" + worldRegionY);
                        e.printStackTrace();
                    }
                    cache = new WorldRegionData(worldRegionX, worldRegionY, save);
                }
            }
            if (cache == null) {
                cache = new WorldRegionData(worldRegionX, worldRegionY, new SaveData("").toLoadData());
            }
            this.loadedCache.put(worldUniqueKey, cache);
        }
        if (loadRegion) {
            cache.loadedRegions.add(GameMath.getUniqueLongKey(regionX, regionY));
        }
        return cache;
    }

    public synchronized void saveAll() {
        for (WorldRegionData data : this.loadedCache.values()) {
            data.saveToFile(this.level);
        }
    }

    public void deleteLevelFiles() {
        Server server = this.level.getServer();
        WorldFileSystem fileSystem = server.world.fileSystem;
        try {
            fileSystem.deleteAllLevelFiles(this.level.getIdentifier());
            SettlementsWorldData.getSettlementsData(server).deleteSettlementsAt(this.level.getIdentifier());
        }
        catch (IOException e) {
            System.err.println("Error deleting files for level " + this.level.getIdentifier());
        }
    }

    public boolean isRegionGenerated(int regionX, int regionY) {
        WorldRegionData cache = this.getWorldRegion(regionX, regionY, false, false);
        return cache.getSaveData(regionX, regionY) != null;
    }

    protected static class WorldRegionData {
        public final int worldRegionX;
        public final int worldRegionY;
        public HashSet<Long> loadedRegions = new HashSet();
        public LoadData save;

        public WorldRegionData(int worldRegionX, int worldRegionY, LoadData save) {
            this.worldRegionX = worldRegionX;
            this.worldRegionY = worldRegionY;
            Objects.requireNonNull(save);
            this.save = save;
        }

        public LoadData getSaveData(int regionX, int regionY) {
            return this.save.getFirstLoadDataByName(regionX + "x" + regionY);
        }

        public void removeRegionAndGetNewSaveData(int regionX, int regionY, Consumer<SaveData> saveHandler) {
            SaveData saveData = this.save.toSaveData();
            saveData.removeFirstSaveDataByName(regionX + "x" + regionY);
            SaveData regionSave = new SaveData(regionX + "x" + regionY);
            saveHandler.accept(regionSave);
            saveData.addSaveData(regionSave);
        }

        public void saveToFile(Level level) {
            WorldFile file = level.getServer().world.fileSystem.getWorldRegionFile(level.getIdentifier(), this.worldRegionX, this.worldRegionY);
            this.save.toSaveData().saveScript(file);
        }
    }
}

