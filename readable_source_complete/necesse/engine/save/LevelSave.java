/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.server.Server;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.LevelEventSave;
import necesse.engine.save.levelData.MobSave;
import necesse.engine.save.levelData.ObjectEntitySave;
import necesse.engine.save.levelData.PickupEntitySave;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.layers.LevelLayer;

public class LevelSave {
    public static PerformanceTimerManager debugLoadingPerformance;

    public static Level loadSave(LoadData save, Server server) {
        try {
            return Performance.recordConstant(debugLoadingPerformance, "levelLoading", () -> {
                Level level = Performance.recordConstant(debugLoadingPerformance, "initial", () -> {
                    int levelID;
                    LevelIdentifier identifier = LevelSave.getLevelSaveIdentifier(save);
                    String levelStringID = save.getUnsafeString("stringID", null, false);
                    if (levelStringID == null) {
                        levelStringID = LevelSave.restoreLevelStringID(save, identifier, server);
                        if (levelStringID == null) {
                            GameLog.warn.println("Could not recover level type for level with identifier " + identifier + ". Using default instead.");
                        } else {
                            GameLog.debug.println("Recovered level type for level with identifier " + identifier + ": " + levelStringID);
                        }
                    }
                    int width = save.getInt("width");
                    int height = save.getInt("height");
                    Level loadedLevel = null;
                    if (levelStringID != null && (levelID = LevelRegistry.getLevelID(levelStringID)) != -1) {
                        loadedLevel = LevelRegistry.getNewLevel(levelID, identifier, width, height, server.world.worldEntity);
                    }
                    if (loadedLevel == null) {
                        loadedLevel = new Level(identifier, width, height, server.world.worldEntity);
                    }
                    loadedLevel.makeServerLevel(server);
                    LoadData modsData = save.getFirstLoadDataByName("MODS");
                    if (modsData != null) {
                        loadedLevel.lastMods = new ArrayList();
                        for (LoadData modData : modsData.getLoadData()) {
                            try {
                                loadedLevel.lastMods.add(ModSaveInfo.fromSave(modData));
                            }
                            catch (LoadDataException e) {
                                GameLog.warn.println("Could not load mod info: " + e.getMessage());
                            }
                        }
                    }
                    return loadedLevel;
                });
                Performance.recordConstant(debugLoadingPerformance, "applyLoadData", () -> level.applyLoadData(save));
                return level;
            });
        }
        catch (Exception e) {
            System.err.println("Level file is corrupt.");
            e.printStackTrace();
            return null;
        }
    }

    public static SaveData getSave(Level level) {
        SaveData save = new SaveData("LEVEL");
        save.addSafeString("gameVersion", "1.0.1");
        save.addUnsafeString("stringID", level.getStringID());
        LevelIdentifier identifier = level.getIdentifier();
        save.addUnsafeString("identifier", identifier.stringID);
        if (identifier.isIslandPosition()) {
            save.addPoint("island", new Point(identifier.getIslandX(), identifier.getIslandY()));
            save.addInt("dimension", identifier.getIslandDimension());
        }
        save.addInt("width", level.tileWidth);
        save.addInt("height", level.tileHeight);
        SaveData modsData = new SaveData("MODS");
        for (LoadedMod mod : ModLoader.getEnabledMods()) {
            SaveData data = mod.getModSaveInfo().getSaveData();
            modsData.addSaveData(data);
        }
        save.addSaveData(modsData);
        level.addSaveData(save);
        return save;
    }

    public static String restoreLevelStringID(LoadData save, LevelIdentifier identifier, Server server) {
        int dimension = identifier.getIslandDimension();
        if (dimension == 1337) {
            return "debug";
        }
        if (dimension == -100) {
            return "dungeon";
        }
        if (dimension == -101) {
            return "dungeonarena";
        }
        if (dimension == -200) {
            return "temple";
        }
        if (dimension == -201) {
            return "templearena";
        }
        Biome biome = LevelSave.getLevelSaveBiome(save);
        if (biome == null) {
            return null;
        }
        if (biome == BiomeRegistry.FOREST || biome == BiomeRegistry.PIRATE_VILLAGE) {
            if (dimension == 0) {
                return "forestsurface";
            }
            if (dimension == -1) {
                return "forestcave";
            }
            if (dimension < -1) {
                return "forestdeepcave";
            }
        } else if (biome == BiomeRegistry.PLAINS) {
            if (dimension == 0) {
                return "plainssurface";
            }
            if (dimension == -1) {
                return "plainscave";
            }
            if (dimension < -1) {
                return "forestdeepcave";
            }
        } else if (biome == BiomeRegistry.SNOW) {
            if (dimension == 0) {
                return "snowsurface";
            }
            if (dimension == -1) {
                return "snowcave";
            }
            if (dimension < -1) {
                return "snowdeepcave";
            }
        } else if (biome == BiomeRegistry.SWAMP) {
            if (dimension == 0) {
                return "swampsurface";
            }
            if (dimension == -1) {
                return "swampcave";
            }
            if (dimension < -1) {
                return "swampdeepcave";
            }
        } else if (biome == BiomeRegistry.DESERT) {
            if (dimension == 0) {
                return "desertsurface";
            }
            if (dimension == -1) {
                return "desertcave";
            }
            if (dimension < -1) {
                return "desertdeepcave";
            }
        }
        return null;
    }

    public static void addLevelBasics(Level level, SaveData save) {
        save.addBoolean("isCave", level.isCave);
        save.addLong("lastWorldTime", level.lastWorldTime);
        save.addUnsafeString("biome", level.baseBiome.getStringID());
        save.addBoolean("isProtected", level.isProtected);
        if (level.fallbackIdentifier != null) {
            save.addUnsafeString("fallbackIdentifier", level.fallbackIdentifier.stringID);
            if (level.fallbackTilePos != null) {
                save.addPoint("fallbackTilePos", level.fallbackTilePos);
            }
        }
        if (!level.childLevels.isEmpty()) {
            save.addStringHashSet("childLevels", level.childLevels.stream().map(i -> i.stringID).collect(Collectors.toCollection(HashSet::new)));
        }
        for (LevelLayer layer : level.layers) {
            layer.addSaveData(save);
        }
        SaveData gndDataSave = new SaveData("GNDData");
        level.gndData.addSaveData(gndDataSave);
        save.addSaveData(gndDataSave);
        SaveData levelDataSave = new SaveData("LEVELDATA");
        level.levelDataManager.addSaveData(levelDataSave);
        save.addSaveData(levelDataSave);
        SaveData events = new SaveData("EVENTS");
        for (LevelEvent event : level.entityManager.events.regionList.getInNoRegion()) {
            Point regionPos;
            if (event.isOver() || !event.shouldSave() || (regionPos = event.getSaveToRegionPos()) != null) continue;
            events.addSaveData(LevelEventSave.getSave(event));
        }
        save.addSaveData(events);
        level.returnedItemsManager.addSaveData(save);
        SaveData stats = new SaveData("STATS");
        level.levelStats.addSaveData(stats);
        save.addSaveData(stats);
    }

    private static boolean placeOldLayerData(Level level, int[] values, SetterFunction<Integer> setter) {
        if (values != null && values.length == level.tileWidth * level.tileHeight) {
            for (int tileX = 0; tileX < level.tileWidth; ++tileX) {
                for (int tileY = 0; tileY < level.tileHeight; ++tileY) {
                    setter.set(tileX, tileY, values[tileX + tileY * level.tileWidth]);
                }
            }
            return true;
        }
        return false;
    }

    private static boolean placeOldLayerData(Level level, byte[] values, SetterFunction<Byte> setter) {
        if (values != null && values.length == level.tileWidth * level.tileHeight) {
            for (int tileX = 0; tileX < level.tileWidth; ++tileX) {
                for (int tileY = 0; tileY < level.tileHeight; ++tileY) {
                    setter.set(tileX, tileY, values[tileX + tileY * level.tileWidth]);
                }
            }
            return true;
        }
        return false;
    }

    private static boolean placeOldLayerData(Level level, boolean[] values, SetterFunction<Boolean> setter) {
        if (values == null) {
            return false;
        }
        if (values.length == level.tileWidth * level.tileHeight) {
            for (int tileX = 0; tileX < level.tileWidth; ++tileX) {
                for (int tileY = 0; tileY < level.tileHeight; ++tileY) {
                    setter.set(tileX, tileY, values[tileX + tileY * level.tileWidth]);
                }
            }
            return true;
        }
        return false;
    }

    private static void loadOldObjectData(Level level, LoadData save, int layerID, String[] objectsFrom, AtomicBoolean printedMigration) {
        if (save == null) {
            return;
        }
        int[] oldObjects = save.getIntArray("objects", null, false);
        if (oldObjects == null) {
            return;
        }
        if (objectsFrom != null) {
            String[] objectsTo = ObjectRegistry.getObjectStringIDs();
            VersionMigration.convertArray(oldObjects, objectsFrom, objectsTo, 0, VersionMigration.oldObjectStringIDs);
        }
        if (LevelSave.placeOldLayerData(level, oldObjects, (int tileX, int tileY, Integer value) -> level.objectLayer.setObject(layerID, tileX, tileY, (int)value)) && !printedMigration.get()) {
            System.out.println("Migrated " + ObjectLayerRegistry.getLayerStringID(layerID) + " object layer on level " + level.getIdentifier().stringID + " from old level system");
            printedMigration.set(true);
        }
        LevelSave.placeOldLayerData(level, save.getByteArray("objectRotations", null, false), (int tileX, int tileY, Byte value) -> level.objectLayer.setObjectRotation(layerID, tileX, tileY, value.byteValue()));
        LevelSave.placeOldLayerData(level, save.getSmallBooleanArray("objectIsPlayerPlaced", null, false), (int tileX, int tileY, Boolean value) -> level.objectLayer.setIsPlayerPlaced(layerID, tileX, tileY, (boolean)value));
    }

    public static void applyLevelBasics(Level level, LoadData save) {
        Performance.recordConstant(debugLoadingPerformance, "basic", () -> {
            LevelIdentifier identifier = level.getIdentifier();
            level.isCave = save.getBoolean("isCave", !identifier.isIslandPosition() || identifier.getIslandDimension() < 0);
            level.lastWorldTime = save.getLong("lastWorldTime", 0L, false);
            Biome biome = LevelSave.getLevelSaveBiome(save);
            if (biome != null) {
                level.baseBiome = biome;
            }
            level.isProtected = save.getBoolean("isProtected", false);
            String fallbackIdentifierString = save.getUnsafeString("fallbackIdentifier", null, false);
            if (fallbackIdentifierString != null) {
                try {
                    level.fallbackIdentifier = new LevelIdentifier(fallbackIdentifierString);
                    level.fallbackTilePos = save.getPoint("fallbackTilePos", null, false);
                }
                catch (InvalidLevelIdentifierException e) {
                    GameLog.warn.println("Loaded invalid fallback for " + level.getIdentifier().stringID + ": " + fallbackIdentifierString);
                }
            }
        });
        level.childLevels.addAll(LevelSave.getChildLevels(save));
        if (level.tileWidth >= 0 && level.tileHeight >= 0) {
            AtomicBoolean printedMigration = new AtomicBoolean(false);
            int[] oldTiles = save.getIntArray("tiles", null, false);
            if (oldTiles != null) {
                String[] tilesFrom = save.getStringArray("tileData", null, false);
                if (tilesFrom != null) {
                    String[] tilesTo = TileRegistry.getTileStringIDs();
                    VersionMigration.convertArray(oldTiles, tilesFrom, tilesTo, 0, VersionMigration.oldTileStringIDs);
                }
                if (LevelSave.placeOldLayerData(level, oldTiles, level.tileLayer::setTile) && !printedMigration.get()) {
                    System.out.println("Migrated tiles on level " + level.getIdentifier().stringID + " from old level system");
                    printedMigration.set(true);
                }
                LevelSave.placeOldLayerData(level, save.getSmallBooleanArray("tileIsPlayerPlaced", null, false), level.tileLayer::setIsPlayerPlaced);
            }
            String[] objectsFrom = save.getStringArray("objectData", null, false);
            LevelSave.loadOldObjectData(level, save, 0, objectsFrom, printedMigration);
            for (int i = 1; i < ObjectLayerRegistry.getTotalLayers(); ++i) {
                String stringID = ObjectLayerRegistry.getLayerStringID(i);
                LevelSave.loadOldObjectData(level, save.getFirstLoadDataByName(stringID + "Objects"), i, objectsFrom, printedMigration);
            }
            if (LevelSave.placeOldLayerData(level, save.getByteArray("wire", null, false), level.regionManager::setWireData) && !printedMigration.get()) {
                System.out.println("Migrated wire data on level " + level.getIdentifier().stringID + " from old level system");
                printedMigration.set(true);
            }
            if (LevelSave.placeOldLayerData(level, save.getSmallBooleanArray("tilesProtected", null, false), level.regionManager::setTileProtected) && !printedMigration.get()) {
                System.out.println("Migrated protected data on level " + level.getIdentifier().stringID + " from old level system");
                printedMigration.set(true);
            }
        }
        Performance.recordConstant(debugLoadingPerformance, "layers", () -> {
            for (LevelLayer layer : level.layers) {
                Performance.recordConstant(debugLoadingPerformance, layer.getStringID(), () -> layer.loadSaveData(save));
            }
        });
        LoadData gndDataSave = save.getFirstLoadDataByName("GNDData");
        if (gndDataSave != null) {
            try {
                level.gndData.applyLoadData(gndDataSave);
            }
            catch (Exception e) {
                System.err.println("Error loading GNDData for level " + level.getIdentifier());
                e.printStackTrace();
            }
        }
        Performance.recordConstant(debugLoadingPerformance, "regions", () -> level.regionManager.calculateRegions());
        Performance.recordConstant(debugLoadingPerformance, "levelData", () -> {
            LoadData levelDataSave = save.getFirstLoadDataByName("LEVELDATA");
            if (levelDataSave != null) {
                level.levelDataManager.loadSaveData(levelDataSave);
            } else {
                System.err.println("Could not load any level data for " + level.getIdentifier());
            }
        });
        Performance.recordConstant(debugLoadingPerformance, "mobs", () -> {
            try {
                List<LoadData> mobs = save.getFirstLoadDataByName("MOBS").getLoadData();
                for (LoadData mob : mobs) {
                    try {
                        Mob loadedMob = MobSave.loadSave(mob, level);
                        if (loadedMob == null) continue;
                        level.regionManager.ensureTileIsLoaded(loadedMob.getTileX(), loadedMob.getTileY());
                        level.entityManager.mobs.addHidden(loadedMob);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        Performance.recordConstant(debugLoadingPerformance, "pickupEntities", () -> {
            try {
                List<LoadData> pickups = save.getFirstLoadDataByName("PICKUPENTITIES").getLoadData();
                for (LoadData pickup : pickups) {
                    try {
                        PickupEntity loadedPickup = PickupEntitySave.loadSave(pickup, level);
                        if (loadedPickup == null) continue;
                        level.regionManager.ensureTileIsLoaded(loadedPickup.getTileX(), loadedPickup.getTileY());
                        level.entityManager.pickups.addHidden(loadedPickup);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        Performance.recordConstant(debugLoadingPerformance, "objectEntities", () -> {
            try {
                List<LoadData> objects = save.getFirstLoadDataByName("OBJECTENTITIES").getLoadData();
                for (LoadData object : objects) {
                    try {
                        ObjectEntity loadedObjectEntity = ObjectEntitySave.loadSave(object, level);
                        if (loadedObjectEntity == null) continue;
                        level.entityManager.objectEntities.addHidden(loadedObjectEntity);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        Performance.recordConstant(debugLoadingPerformance, "events", () -> {
            try {
                List<LoadData> events = save.getFirstLoadDataByName("EVENTS").getLoadData();
                for (LoadData event : events) {
                    try {
                        LevelEvent loadedLevelEvent = LevelEventSave.loadSaveData(event, level);
                        if (loadedLevelEvent == null) continue;
                        level.entityManager.events.addHidden(loadedLevelEvent);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                System.err.println("Could not load level events, resulting in a wipe.");
            }
        });
        Performance.recordConstant(debugLoadingPerformance, "returnedItems", () -> {
            try {
                level.returnedItemsManager.loadSaveData(save);
            }
            catch (Exception e) {
                System.err.println("Could not load returned items, resulting in a wipe.");
            }
        });
        Performance.recordConstant(debugLoadingPerformance, "stats", () -> {
            try {
                LoadData statsData = save.getFirstLoadDataByName("STATS");
                if (statsData != null) {
                    level.levelStats.applyLoadData(statsData);
                }
            }
            catch (Exception e) {
                System.err.println("Error loading level stats for " + level.getIdentifier());
                e.printStackTrace();
            }
        });
    }

    public static int getMigratedBiomeID(String biomeStringID, boolean printMigration) {
        int id;
        block3: {
            id = -1;
            try {
                id = BiomeRegistry.getBiomeIDRaw(biomeStringID);
            }
            catch (NoSuchElementException e) {
                String newStingID = VersionMigration.tryFixStringID(biomeStringID, VersionMigration.oldBiomeStringIDs);
                if (biomeStringID.equals(newStingID)) break block3;
                if (printMigration) {
                    System.out.println("Migrated biome from " + biomeStringID + " to " + newStingID);
                }
                id = BiomeRegistry.getBiomeID(newStingID);
            }
        }
        return id;
    }

    public static Biome getLevelSaveBiomeRaw(LoadData save, boolean printMigration) {
        String stringID = save.getUnsafeString("biome");
        int id = LevelSave.getMigratedBiomeID(stringID, printMigration);
        return BiomeRegistry.getBiome(id);
    }

    public static Biome getLevelSaveBiome(LoadData save) {
        try {
            return LevelSave.getLevelSaveBiomeRaw(save, true);
        }
        catch (Exception e) {
            System.err.println("Could not load level biome");
            return null;
        }
    }

    public static LevelIdentifier getLevelSaveIdentifier(LoadData save) {
        LevelIdentifier identifier;
        try {
            identifier = new LevelIdentifier(save.getUnsafeString("identifier", null, false));
        }
        catch (InvalidLevelIdentifierException e) {
            Point island = save.getPoint("island");
            int dimension = save.getInt("dimension");
            identifier = new LevelIdentifier(island.x, island.y, dimension);
        }
        return identifier;
    }

    public static HashSet<LevelIdentifier> getChildLevels(LoadData save) {
        HashSet<LevelIdentifier> out = new HashSet<LevelIdentifier>();
        HashSet<String> childLevels = save.getStringHashSet("childLevels", null, false);
        if (childLevels != null) {
            for (String childLevelIdentifier : childLevels) {
                try {
                    out.add(new LevelIdentifier(childLevelIdentifier));
                }
                catch (Exception exception) {}
            }
        }
        return out;
    }

    private static interface SetterFunction<T> {
        public void set(int var1, int var2, T var3);
    }
}

