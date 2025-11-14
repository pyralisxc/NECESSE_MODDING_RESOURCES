/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.GameRandomNoise;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.LevelEventSave;
import necesse.engine.save.levelData.MobSave;
import necesse.engine.save.levelData.PickupEntitySave;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashMap;
import necesse.engine.world.World;
import necesse.engine.world.WorldFile;
import necesse.engine.world.WorldGenerator;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.DungeonEntranceObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TempleEntranceObjectEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;

public class OneWorldMigration {
    private static final int ISLAND_TILE_SIZE = 300;
    private static final int ISLAND_BIOME_BLENDING_TILES = 20;
    private static final double ISLAND_NOISE_MOD = 0.023333333333333334;
    public final Server server;
    public final World world;
    private GameRandomNoise islandEdgeNoise;
    private BiomeGeneratorStack generatorStack;
    private LinkedList<LevelFile> levelFiles;
    private PointHashMap<HashMap<LevelIdentifier, LevelFile>> levelFilesByIsland;
    private PointHashMap<Point> islandPlacementsMap;
    private PointHashMap<Biome> islandBiomes;
    private LevelIdentifier spawnLevelIdentifier;
    private HashMap<LevelIdentifier, LevelIdentifier> oldToNewLevelIdentifiers;
    private HashMap<LevelIdentifier, Integer> settlementPositionToUniqueIDs;
    private HashMap<LevelIdentifier, Level> newLevels;
    private HashMap<LevelIdentifier, Point> placedLevelTileOffsets;
    private HashSet<LevelIdentifier> foundOpenIncursions;

    public OneWorldMigration(World world) {
        this.server = world.server;
        this.world = world;
    }

    public void run() {
        this.setStartingMessage(new LocalMessage("loading", "gatheringlevels"));
        this.spawnLevelIdentifier = this.world.worldEntity.spawnLevelIdentifier;
        if (!this.spawnLevelIdentifier.isIslandPosition()) {
            this.spawnLevelIdentifier = new LevelIdentifier(0, 0, 0);
        }
        this.islandEdgeNoise = new GameRandomNoise(this.spawnLevelIdentifier.stringID.hashCode());
        this.generatorStack = this.world.worldEntity.getGeneratorStack();
        this.world.worldEntity.spawnLevelIdentifier = LevelIdentifier.SURFACE_IDENTIFIER;
        this.world.worldEntity.spawnTile = new Point(150, 150);
        this.levelFiles = new LinkedList();
        this.levelFilesByIsland = new PointHashMap();
        this.islandPlacementsMap = new PointHashMap();
        this.islandBiomes = new PointHashMap();
        this.oldToNewLevelIdentifiers = new HashMap();
        this.settlementPositionToUniqueIDs = new HashMap();
        this.newLevels = new HashMap();
        this.placedLevelTileOffsets = new HashMap();
        this.foundOpenIncursions = new HashSet();
        int gatheringLevelsProgress = 0;
        for (WorldFile file : this.world.fileSystem.getLevelFiles()) {
            this.setStartingMessageValue(new LocalMessage("loading", "gatheringlevels"), gatheringLevelsProgress++);
            LoadData save = this.server.world.loadLevelScript(file);
            try {
                LevelFile levelFile = new LevelFile(file, save);
                LevelIdentifier levelIdentifier = levelFile.levelIdentifier;
                this.levelFiles.add(levelFile);
                if (!levelIdentifier.isIslandPosition()) continue;
                if (levelIdentifier.equals(this.spawnLevelIdentifier)) {
                    this.islandPlacementsMap.put(levelIdentifier.getIslandX(), levelIdentifier.getIslandY(), new Point(0, 0));
                    this.islandBiomes.put(0, 0, levelFile.biome);
                }
                HashMap map = this.levelFilesByIsland.compute(levelIdentifier.getIslandX(), levelIdentifier.getIslandY(), (k, v) -> {
                    if (v == null) {
                        v = new HashMap();
                    }
                    return v;
                });
                map.put(levelIdentifier, levelFile);
            }
            catch (Exception e) {
                System.err.println("Error loading level file: " + file.getFileName());
                e.printStackTrace();
            }
        }
        Comparator<LevelFile> comparator = Comparator.comparingInt(f -> f.levelIdentifier.isIslandPosition() ? 0 : 1);
        comparator = comparator.thenComparingInt(f -> {
            if (!f.levelIdentifier.isIslandPosition()) {
                return Integer.MAX_VALUE;
            }
            return (int)GameMath.squareDistance(this.spawnLevelIdentifier.getIslandX(), this.spawnLevelIdentifier.getIslandY(), f.levelIdentifier.getIslandX(), f.levelIdentifier.getIslandY());
        });
        comparator = comparator.thenComparingInt(f -> {
            if (f.levelIdentifier.isIslandPosition()) {
                return Integer.MAX_VALUE;
            }
            return -f.levelIdentifier.getIslandDimension();
        });
        this.levelFiles.sort(comparator);
        int findingSpotProgress = 0;
        block7: for (LevelFile levelFile : this.levelFiles) {
            float currentProgress = (float)findingSpotProgress++ / (float)this.levelFiles.size();
            if (!levelFile.levelIdentifier.isIslandPosition() || this.islandPlacementsMap.containsKey(levelFile.levelIdentifier.getIslandX(), levelFile.levelIdentifier.getIslandY())) continue;
            this.setStartingMessagePercent(new LocalMessage("loading", "findingspot", "level", levelFile.levelIdentifier.stringID), currentProgress);
            int deltaFromSpawnX = levelFile.levelIdentifier.getIslandX() - this.spawnLevelIdentifier.getIslandX();
            int deltaFromSpawnY = levelFile.levelIdentifier.getIslandY() - this.spawnLevelIdentifier.getIslandY();
            Point2D.Float dir = GameMath.normalize(deltaFromSpawnX, deltaFromSpawnY);
            double currentDistance = 0.0;
            while (true) {
                int currentY;
                int currentX;
                if (!this.islandBiomes.containsKey(currentX = (int)Math.floor((double)dir.x * currentDistance), currentY = (int)Math.floor((double)dir.y * currentDistance))) {
                    this.islandPlacementsMap.put(levelFile.levelIdentifier.getIslandX(), levelFile.levelIdentifier.getIslandY(), new Point(currentX, currentY));
                    this.islandBiomes.put(currentX, currentY, levelFile.biome);
                    continue block7;
                }
                currentDistance += 1.0;
            }
        }
        LinkedList<LevelFile> unplacedLevels = new LinkedList<LevelFile>(this.levelFiles);
        while (!unplacedLevels.isEmpty()) {
            int lastUnplacedSize = unplacedLevels.size();
            ListIterator li = unplacedLevels.listIterator();
            while (li.hasNext()) {
                float currentProgress = (float)(this.levelFiles.size() - unplacedLevels.size()) / (float)this.levelFiles.size();
                LevelFile levelFile = (LevelFile)li.next();
                this.setStartingMessagePercent(new LocalMessage("loading", "migratinglevel", "level", levelFile.levelIdentifier.stringID), currentProgress);
                NewLevelOptions newLevelOptions = this.getNewLevelOptions(levelFile.levelIdentifier);
                if (newLevelOptions == null) continue;
                try {
                    int oldIslandY;
                    Level oldLevel = LevelSave.loadSave(levelFile.save, this.server);
                    if (oldLevel == null) {
                        System.err.println("Could not load level: " + levelFile.levelIdentifier.stringID);
                        continue;
                    }
                    LevelIdentifier newLevelIdentifier = newLevelOptions.newLevelIdentifier;
                    this.oldToNewLevelIdentifiers.put(levelFile.levelIdentifier, newLevelIdentifier);
                    if (newLevelOptions.keepOldLevel) {
                        oldLevel.overwriteIdentifier(newLevelIdentifier);
                        oldLevel.makeServerLevel(this.server);
                        oldLevel.setWorldEntity(this.server.world.worldEntity);
                        oldLevel.entityManager.refreshSetLevel();
                        this.migrateEntireOldLevel(levelFile, levelFile.levelIdentifier, oldLevel);
                        this.world.levelManager.overwriteLevel(oldLevel);
                        this.world.levelManager.unloadLevel(oldLevel);
                        System.out.println("CONVERTED " + levelFile.levelIdentifier + " TO " + newLevelIdentifier.stringID);
                        this.placedLevelTileOffsets.put(levelFile.levelIdentifier, new Point(0, 0));
                        if (!levelFile.levelIdentifier.equals(newLevelIdentifier.stringID)) {
                            this.server.world.fileSystem.deleteAllLevelFiles(levelFile.levelIdentifier);
                        }
                        li.remove();
                        continue;
                    }
                    if (!levelFile.levelIdentifier.isIslandPosition()) continue;
                    int oldIslandX = levelFile.levelIdentifier.getIslandX();
                    Point newIsland = this.islandPlacementsMap.get(oldIslandX, oldIslandY = levelFile.levelIdentifier.getIslandY());
                    if (newIsland == null) {
                        System.err.println("No placement found for level: " + levelFile.levelIdentifier.stringID);
                        continue;
                    }
                    Level newLevel = this.newLevels.get(newLevelIdentifier);
                    if (newLevel == null) {
                        newLevel = WorldGenerator.generateNewLevel(newLevelIdentifier, this.server, new GameBlackboard());
                        newLevel.overwriteIdentifier(newLevelIdentifier);
                        newLevel.makeServerLevel(this.server);
                        newLevel.setWorldEntity(this.server.world.worldEntity);
                        newLevel.entityManager.refreshSetLevel();
                        this.newLevels.put(newLevelIdentifier, newLevel);
                    }
                    this.placeLevelOnNewLevel(levelFile, newLevel, oldLevel, oldIslandX, oldIslandY, newIsland.x, newIsland.y);
                    System.out.println("PLACED " + levelFile.levelIdentifier + " AT " + newIsland.x + "x" + newIsland.y + " (" + newLevelIdentifier.stringID + ")");
                    this.server.world.fileSystem.deleteAllLevelFiles(levelFile.levelIdentifier);
                }
                catch (Exception e) {
                    System.err.println("Error placing level file: " + levelFile.levelIdentifier.stringID);
                    e.printStackTrace();
                }
                li.remove();
            }
            boolean placedAny = lastUnplacedSize != unplacedLevels.size();
            if (placedAny) continue;
            System.out.println("Could not place following levels: " + Arrays.toString(unplacedLevels.stream().map(f -> f.levelIdentifier).toArray()));
            break;
        }
        for (Level newLevel : this.newLevels.values()) {
            this.world.levelManager.overwriteLevel(newLevel);
        }
        SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(this.world.worldEntity);
        settlementsData.runFinalSettlementMigrations(this);
        for (WorldFile file : this.world.fileSystem.getPlayerFiles()) {
            this.setStartingMessage(new LocalMessage("loading", "migratingplayers"));
            try {
                String authString = GameUtils.removeFileExtension(file.getFileName().toString());
                long auth = Long.parseLong(authString);
                ServerClient serverClient = this.world.loadClient(file, 0L, null, 0, auth);
                this.world.savePlayer(serverClient);
            }
            catch (Exception e) {
                System.err.println("Error migrating player file: " + file.getFileName());
                e.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private NewLevelOptions getNewLevelOptions(LevelIdentifier oldLevelIdentifier) {
        LevelIdentifier newLevelIdentifier = this.getNewLevelIdentifier(oldLevelIdentifier);
        if (newLevelIdentifier != null) {
            return new NewLevelOptions(newLevelIdentifier, false);
        }
        if (this.foundOpenIncursions.contains(oldLevelIdentifier)) {
            return new NewLevelOptions(oldLevelIdentifier, true);
        }
        if (oldLevelIdentifier.isIslandPosition()) {
            if (oldLevelIdentifier.getIslandDimension() == -100 || oldLevelIdentifier.getIslandDimension() == -101) {
                if (!this.placedLevelTileOffsets.containsKey(new LevelIdentifier(oldLevelIdentifier.getIslandX(), oldLevelIdentifier.getIslandY(), 0))) {
                    return null;
                }
                Level surfaceLevel = this.newLevels.get(LevelIdentifier.SURFACE_IDENTIFIER);
                if (surfaceLevel != null) {
                    System.out.println("LOOKING FOR DUNGEON ENTRANCE FOR " + oldLevelIdentifier);
                    int entranceObjectID = ObjectRegistry.getObjectID("dungeonentrance");
                    Point offset = this.getTilePositionOffset(oldLevelIdentifier);
                    try {
                        this.ensureTilesLoaded(surfaceLevel, offset.x, offset.y, offset.x + 300 - 1, offset.y + 300 - 1);
                        for (int tileX = 0; tileX < 300; ++tileX) {
                            for (int tileY = 0; tileY < 300; ++tileY) {
                                int checkTileX = offset.x + tileX;
                                int checkTileY = offset.y + tileY;
                                if (surfaceLevel.getObjectID(0, checkTileX, checkTileY) != entranceObjectID) continue;
                                LevelIdentifier dungeonIdentifier = DungeonEntranceObjectEntity.getDungeonLevelIdentifier(LevelIdentifier.SURFACE_IDENTIFIER, checkTileX, checkTileY);
                                if (oldLevelIdentifier.getIslandDimension() == -101) {
                                    LevelIdentifier dungeonArenaIdentifier = DungeonEntranceObjectEntity.getDungeonArenaLevelIdentifier(dungeonIdentifier);
                                    NewLevelOptions newLevelOptions = new NewLevelOptions(dungeonArenaIdentifier, true);
                                    return newLevelOptions;
                                }
                                NewLevelOptions dungeonArenaIdentifier = new NewLevelOptions(dungeonIdentifier, true);
                                return dungeonArenaIdentifier;
                            }
                        }
                    }
                    finally {
                        this.unloadTiles(surfaceLevel, offset.x, offset.y, offset.x + 300 - 1, offset.y + 300 - 1);
                    }
                }
            } else if (oldLevelIdentifier.getIslandDimension() == -200 || oldLevelIdentifier.getIslandDimension() == -201) {
                if (!this.placedLevelTileOffsets.containsKey(new LevelIdentifier(oldLevelIdentifier.getIslandX(), oldLevelIdentifier.getIslandY(), -2))) {
                    return null;
                }
                Level surfaceLevel = this.newLevels.get(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
                if (surfaceLevel != null) {
                    System.out.println("LOOKING FOR TEMPLE ENTRANCE FOR " + oldLevelIdentifier);
                    int entranceObjectID = ObjectRegistry.getObjectID("templeentrance");
                    Point offset = this.getTilePositionOffset(oldLevelIdentifier);
                    try {
                        this.ensureTilesLoaded(surfaceLevel, offset.x, offset.y, offset.x + 300 - 1, offset.y + 300 - 1);
                        for (int tileX = 0; tileX < 300; ++tileX) {
                            for (int tileY = 0; tileY < 300; ++tileY) {
                                int checkTileX = offset.x + tileX;
                                int checkTileY = offset.y + tileY;
                                if (surfaceLevel.getObjectID(0, checkTileX, checkTileY) != entranceObjectID) continue;
                                LevelIdentifier templeIdentifier = TempleEntranceObjectEntity.getTempleLevelIdentifier(LevelIdentifier.SURFACE_IDENTIFIER, checkTileX, checkTileY);
                                if (oldLevelIdentifier.getIslandDimension() == -201) {
                                    LevelIdentifier templeArenaIdentifier = TempleEntranceObjectEntity.getTempleArenaLevelIdentifier(templeIdentifier);
                                    NewLevelOptions newLevelOptions = new NewLevelOptions(templeArenaIdentifier, true);
                                    return newLevelOptions;
                                }
                                NewLevelOptions newLevelOptions = new NewLevelOptions(templeIdentifier, true);
                                return newLevelOptions;
                            }
                        }
                    }
                    finally {
                        this.unloadTiles(surfaceLevel, offset.x, offset.y, offset.x + 300 - 1, offset.y + 300 - 1);
                    }
                }
            }
        }
        return null;
    }

    public LevelIdentifier getNewLevelIdentifier(LevelIdentifier oldLevelIdentifier) {
        LevelIdentifier newLevelIdentifier = this.oldToNewLevelIdentifiers.get(oldLevelIdentifier);
        if (newLevelIdentifier != null) {
            return newLevelIdentifier;
        }
        if (oldLevelIdentifier.isIslandPosition()) {
            if (oldLevelIdentifier.getIslandDimension() == 0) {
                return LevelIdentifier.SURFACE_IDENTIFIER;
            }
            if (oldLevelIdentifier.getIslandDimension() == -1) {
                return LevelIdentifier.CAVE_IDENTIFIER;
            }
            if (oldLevelIdentifier.getIslandDimension() == -2) {
                return LevelIdentifier.DEEP_CAVE_IDENTIFIER;
            }
        }
        return null;
    }

    public Point getTilePositionOffset(LevelIdentifier oldLevelIdentifier) {
        if (oldLevelIdentifier == null) {
            return new Point(0, 0);
        }
        if (!oldLevelIdentifier.isIslandPosition()) {
            return new Point(0, 0);
        }
        Point placedOffset = this.placedLevelTileOffsets.get(oldLevelIdentifier);
        if (placedOffset != null) {
            return placedOffset;
        }
        Point newIsland = this.islandPlacementsMap.get(oldLevelIdentifier.getIslandX(), oldLevelIdentifier.getIslandY());
        if (newIsland == null) {
            return new Point(0, 0);
        }
        return new Point(newIsland.x * 300, newIsland.y * 300);
    }

    public Point getLevelPositionOffset(LevelIdentifier oldLevelIdentifier) {
        Point tileOffset = this.getTilePositionOffset(oldLevelIdentifier);
        return new Point(tileOffset.x * 32, tileOffset.y * 32);
    }

    public void setOldSettlementLevelIdentifier(LevelIdentifier oldSettlementLevelIdentifier, int newUniqueID) {
        this.settlementPositionToUniqueIDs.put(oldSettlementLevelIdentifier, newUniqueID);
    }

    public int getOldSettlementAtLevelUniqueID(LevelIdentifier levelIdentifier) {
        return this.settlementPositionToUniqueIDs.getOrDefault(levelIdentifier, 0);
    }

    public void addFoundOpenIncursion(LevelIdentifier levelIdentifier) {
        this.foundOpenIncursions.add(levelIdentifier);
    }

    protected void setStartingMessage(GameMessage message) {
        GameMessageBuilder builder = new GameMessageBuilder().append("loading", "migratingworld");
        if (message != null) {
            builder.append("\n").append(message);
        }
        this.server.setStartingMessage(builder, false);
    }

    protected void setStartingMessage(String category, String key) {
        this.setStartingMessage(new LocalMessage(category, key));
    }

    protected void setStartingMessagePercent(GameMessage message, float percentProgress) {
        GameMessageBuilder builder = new GameMessageBuilder().append(message).append(" " + (int)(percentProgress * 100.0f) + "%");
        this.setStartingMessage(builder);
    }

    protected void setStartingMessagePercent(String category, String key, float percentProgress) {
        this.setStartingMessagePercent(new LocalMessage(category, key), percentProgress);
    }

    protected void setStartingMessageValue(GameMessage message, int value) {
        GameMessageBuilder builder = new GameMessageBuilder().append(message).append(" " + value);
        this.setStartingMessage(builder);
    }

    protected void setStartingMessageValue(String category, String key, int value) {
        this.setStartingMessageValue(new LocalMessage(category, key), value);
    }

    public boolean isRegionStartTile(int tileCoordinate, int regionCoordinate) {
        return tileCoordinate == GameMath.getTileCoordByRegion(regionCoordinate);
    }

    public boolean isRegionEndTile(int tileCoordinate, int regionCoordinate) {
        return tileCoordinate == GameMath.getTileCoordByRegion(regionCoordinate) + 16 - 1;
    }

    protected void placeLevelOnNewLevel(LevelFile levelFile, Level newLevel, Level islandLevel, int oldIslandX, int oldIslandY, int newIslandX, int newIslandY) {
        LoadData settlementLevelDataSave;
        LoadData levelDataSave;
        int islandStartTileX = newIslandX * 300;
        int islandStartTileY = newIslandY * 300;
        int regionStartX = newLevel.regionManager.getRegionCoordByTile(islandStartTileX);
        int regionStartY = newLevel.regionManager.getRegionCoordByTile(islandStartTileY);
        int regionEndX = newLevel.regionManager.getRegionCoordByTile(islandStartTileX + 300 - 1);
        int regionEndY = newLevel.regionManager.getRegionCoordByTile(islandStartTileY + 300 - 1);
        boolean regionStartXIsEdge = !this.isRegionStartTile(islandStartTileX, regionStartX);
        boolean regionStartYIsEdge = !this.isRegionStartTile(islandStartTileY, regionStartY);
        boolean regionEndXIsEdge = !this.isRegionEndTile(islandStartTileX + 300 - 1, regionEndX);
        boolean regionEndYIsEdge = !this.isRegionEndTile(islandStartTileY + 300 - 1, regionEndY);
        for (int regionX = regionStartX; regionX <= regionEndX; ++regionX) {
            for (int regionY = regionStartY; regionY <= regionEndY; ++regionY) {
                if (regionStartXIsEdge && regionX == regionStartX || regionStartYIsEdge && regionY == regionStartY || regionEndXIsEdge && regionX == regionEndX || regionEndYIsEdge && regionY == regionEndY) {
                    newLevel.regionManager.ensureRegionIsLoaded(regionX, regionY);
                    continue;
                }
                newLevel.regionManager.ensureRegionIsLoadedButDontGenerate(regionX, regionY);
            }
        }
        PresetUtils.clearMobsTilRectangle(newLevel, new Rectangle(islandStartTileX, islandStartTileY, 300, 300));
        Point tileOffset = new Point(islandStartTileX, islandStartTileY);
        Point positionOffset = new Point(islandStartTileX * 32, islandStartTileY * 32);
        Biome islandBiome = this.islandBiomes.get(newIslandX, newIslandY);
        RegionBoundsExecutor newLevelExecutor = new RegionBoundsExecutor(newLevel.regionManager, islandStartTileX, islandStartTileY, islandStartTileX + 300 - 1, islandStartTileY + 300 - 1, true);
        RegionBoundsExecutor oldLevelExecutor = new RegionBoundsExecutor(islandLevel.regionManager, 0, 0, islandLevel.tileWidth - 1, islandLevel.tileHeight - 1, true);
        for (int islandTileX = 0; islandTileX < 300; ++islandTileX) {
            for (int islandTileY = 0; islandTileY < 300; ++islandTileY) {
                int tileX = islandStartTileX + islandTileX;
                int tileY = islandStartTileY + islandTileY;
                Region oldRegion = (Region)oldLevelExecutor.getRegionByTile(islandTileX, islandTileY);
                int oldRegionTileX = islandTileX - oldRegion.tileXOffset;
                int oldRegionTileY = islandTileY - oldRegion.tileYOffset;
                Region nextRegion = (Region)newLevelExecutor.getRegionByTile(tileX, tileY);
                int regionTileX = tileX - nextRegion.tileXOffset;
                int regionTileY = tileY - nextRegion.tileYOffset;
                Biome biome = this.getEdgeBiome(newIslandX, newIslandY, islandTileX, islandTileY, islandBiome);
                nextRegion.biomeLayer.setBiomeByRegion(regionTileX, regionTileY, biome.getID(), true);
                nextRegion.tileLayer.setTileByRegion(regionTileX, regionTileY, oldRegion.tileLayer.getTileIDByRegion(oldRegionTileX, oldRegionTileY), true);
                nextRegion.tileLayer.setIsPlayerPlacedByRegion(regionTileX, regionTileY, oldRegion.tileLayer.isPlayerPlacedByRegion(oldRegionTileX, oldRegionTileY));
                for (int objectLayer = 0; objectLayer < ObjectLayerRegistry.getTotalLayers(); ++objectLayer) {
                    nextRegion.objectLayer.setObjectByRegion(objectLayer, regionTileX, regionTileY, oldRegion.objectLayer.getObjectIDByRegion(objectLayer, oldRegionTileX, oldRegionTileY), true);
                    nextRegion.objectLayer.setObjectRotationByRegion(objectLayer, regionTileX, regionTileY, oldRegion.objectLayer.getObjectRotationByRegion(objectLayer, oldRegionTileX, oldRegionTileY));
                    nextRegion.objectLayer.setIsPlayerPlacedByRegion(objectLayer, regionTileX, regionTileY, oldRegion.objectLayer.isPlayerPlacedByRegion(objectLayer, oldRegionTileX, oldRegionTileY));
                }
                nextRegion.tilesProtectedLayer.setTileProtectedByRegion(regionTileX, regionTileY, oldRegion.tilesProtectedLayer.isTileProtectedByRegion(oldRegionTileX, oldRegionTileY));
                nextRegion.wireLayer.setWireDataByRegion(regionTileX, regionTileY, oldRegion.wireLayer.getWireDataByRegion(oldRegionTileX, oldRegionTileY));
                if (!oldRegion.logicLayer.hasLogicGateByRegion(oldRegionTileX, oldRegionTileY)) continue;
                int logicGateID = oldRegion.logicLayer.getLogicGateIDByRegion(oldRegionTileX, oldRegionTileY);
                nextRegion.logicLayer.setLogicGateByRegion(regionTileX, regionTileY, logicGateID, null);
                LogicGateEntity oldEntity = oldRegion.logicLayer.getEntityByRegion(oldRegionTileX, oldRegionTileY);
                LogicGateEntity newEntity = nextRegion.logicLayer.getEntityByRegion(regionTileX, regionTileY);
                if (oldEntity == null || newEntity == null) continue;
                SaveData save = new SaveData("");
                oldEntity.addSaveData(save);
                newEntity.applyLoadData(save.toLoadData());
                newEntity.migrateToOneWorld(this, islandLevel.getIdentifier(), tileOffset, positionOffset);
            }
        }
        LoadData settlementLayerSave = levelFile.save.getFirstLoadDataByName("settlement");
        if (settlementLayerSave != null && (levelDataSave = levelFile.save.getFirstLoadDataByName("LEVELDATA")) != null && (settlementLevelDataSave = levelDataSave.getFirstLoadDataByName("settlement")) != null) {
            SettlementsWorldData.getSettlementsData(newLevel).migrateFromOldSettlementSystem(this, islandLevel.getIdentifier(), newLevel, settlementLayerSave, settlementLevelDataSave, tileOffset, positionOffset);
        }
        islandLevel.levelDataManager.migrateToOneWorld(this, islandLevel.getIdentifier(), newLevel, tileOffset, positionOffset);
        for (Mob mob : islandLevel.entityManager.mobs) {
            SaveData save;
            Mob newMob;
            if (!mob.shouldSave() || (newMob = MobSave.loadSave((save = MobSave.getSave("MOB", mob)).toLoadData(), newLevel)) == null) continue;
            newMob.migrateToOneWorld(this, islandLevel.getIdentifier(), tileOffset, positionOffset, mob);
            newLevel.entityManager.mobs.addHidden(newMob);
        }
        for (PickupEntity pickup : islandLevel.entityManager.pickups) {
            SaveData save;
            PickupEntity newPickup;
            if (!pickup.shouldSave() || (newPickup = PickupEntitySave.loadSave((save = PickupEntitySave.getSave(pickup)).toLoadData(), newLevel)) == null) continue;
            newPickup.migrateToOneWorld(this, islandLevel.getIdentifier(), tileOffset, positionOffset);
            newLevel.entityManager.pickups.addHidden(newPickup);
        }
        for (ObjectEntity oldObjectEntity : islandLevel.entityManager.objectEntities) {
            GameObject object = oldObjectEntity.getObject();
            int tileX = islandStartTileX + oldObjectEntity.tileX;
            int tileY = islandStartTileY + oldObjectEntity.tileY;
            Region nextRegion = (Region)newLevelExecutor.getRegionByTile(tileX, tileY);
            int regionTileX = tileX - nextRegion.tileXOffset;
            int regionTileY = tileY - nextRegion.tileYOffset;
            int objectID = nextRegion.objectLayer.getObjectIDByRegion(0, regionTileX, regionTileY);
            if (objectID != object.getID()) continue;
            SaveData save = new SaveData("");
            oldObjectEntity.addSaveData(save);
            ObjectEntity newObjectEntity = object.getNewObjectEntity(newLevel, tileX, tileY);
            if (newObjectEntity == null) continue;
            if (!newObjectEntity.type.equals(oldObjectEntity.type)) {
                System.err.println("Migrated object entity on " + tileX + "x" + tileY + " on from " + islandLevel.getIdentifier() + " was invalid");
                continue;
            }
            newObjectEntity.applyLoadData(save.toLoadData());
            newObjectEntity.migrateToOneWorld(this, islandLevel.getIdentifier(), tileOffset, positionOffset);
            newLevel.entityManager.objectEntities.addHidden(newObjectEntity);
        }
        for (LevelEvent event : islandLevel.entityManager.events) {
            SaveData save;
            LevelEvent newEvent;
            if (!event.shouldSave() || (newEvent = LevelEventSave.loadSaveData((save = LevelEventSave.getSave(event)).toLoadData(), newLevel)) == null) continue;
            newEvent.migrateToOneWorld(this, islandLevel.getIdentifier(), tileOffset, positionOffset);
            newLevel.entityManager.events.addHidden(newEvent);
        }
        for (int regionX = regionStartX; regionX <= regionEndX; ++regionX) {
            for (int regionY = regionStartY; regionY <= regionEndY; ++regionY) {
                newLevel.regionManager.unloadRegion(regionX, regionY);
            }
        }
        this.placedLevelTileOffsets.put(islandLevel.getIdentifier(), new Point(islandStartTileX, islandStartTileY));
    }

    protected void migrateEntireOldLevel(LevelFile levelFile, LevelIdentifier oldLevelIdentifier, Level level) {
        if (level.tileWidth <= 0 || level.tileHeight <= 0) {
            System.err.println("Could not migrate infinite level: " + levelFile.levelIdentifier.stringID);
        }
        this.ensureTilesLoaded(level, 0, 0, level.tileWidth - 1, level.tileHeight - 1);
        Point tileOffset = new Point(0, 0);
        Point positionOffset = new Point(0, 0);
        RegionBoundsExecutor executor = new RegionBoundsExecutor(level.regionManager, 0, 0, level.tileWidth - 1, level.tileHeight - 1, true);
        level.levelDataManager.migrateToOneWorld(this, oldLevelIdentifier, level, tileOffset, positionOffset);
        for (Mob mob : level.entityManager.mobs) {
            if (!mob.shouldSave()) {
                mob.remove();
                continue;
            }
            mob.migrateToOneWorld(this, oldLevelIdentifier, tileOffset, positionOffset, mob);
        }
        for (PickupEntity pickup : level.entityManager.pickups) {
            if (!pickup.shouldSave()) {
                pickup.remove();
                continue;
            }
            pickup.migrateToOneWorld(this, oldLevelIdentifier, tileOffset, positionOffset);
        }
        for (ObjectEntity objectEntity : level.entityManager.objectEntities) {
            GameObject object = objectEntity.getObject();
            int tileX = objectEntity.tileX;
            int tileY = objectEntity.tileY;
            Region nextRegion = (Region)executor.getRegionByTile(tileX, tileY);
            int regionTileX = tileX - nextRegion.tileXOffset;
            int regionTileY = tileY - nextRegion.tileYOffset;
            int objectID = nextRegion.objectLayer.getObjectIDByRegion(0, regionTileX, regionTileY);
            if (objectID != object.getID()) continue;
            objectEntity.migrateToOneWorld(this, oldLevelIdentifier, tileOffset, positionOffset);
        }
        for (LevelEvent event : level.entityManager.events) {
            if (!event.shouldSave()) {
                event.over();
                continue;
            }
            event.migrateToOneWorld(this, oldLevelIdentifier, tileOffset, positionOffset);
        }
        level.migrateToOldLevel(this, oldLevelIdentifier);
        this.unloadTiles(level, 0, 0, level.tileWidth - 1, level.tileHeight - 1);
    }

    private void ensureTilesLoaded(Level level, int startTileX, int startTileY, int endTileX, int endTileY) {
        int regionStartX = level.regionManager.getRegionCoordByTile(startTileX);
        int regionStartY = level.regionManager.getRegionCoordByTile(startTileY);
        int regionEndX = level.regionManager.getRegionCoordByTile(endTileX);
        int regionEndY = level.regionManager.getRegionCoordByTile(endTileY);
        for (int regionX = regionStartX; regionX <= regionEndX; ++regionX) {
            for (int regionY = regionStartY; regionY <= regionEndY; ++regionY) {
                level.regionManager.ensureRegionIsLoaded(regionX, regionY);
            }
        }
    }

    private void unloadTiles(Level level, int startTileX, int startTileY, int endTileX, int endTileY) {
        int regionStartX = level.regionManager.getRegionCoordByTile(startTileX);
        int regionStartY = level.regionManager.getRegionCoordByTile(startTileY);
        int regionEndX = level.regionManager.getRegionCoordByTile(endTileX);
        int regionEndY = level.regionManager.getRegionCoordByTile(endTileY);
        for (int regionX = regionStartX; regionX <= regionEndX; ++regionX) {
            for (int regionY = regionStartY; regionY <= regionEndY; ++regionY) {
                level.regionManager.unloadRegion(regionX, regionY);
            }
        }
    }

    private Biome getEdgeBiome(int islandX, int islandY, int islandTileX, int islandTileY, Biome baseBiome) {
        double perlinEdgePercent;
        double perlin;
        double edgePercent;
        double bestEdgePercent = 0.0;
        Biome bestEdgeBiome = baseBiome;
        if (islandTileX <= 20) {
            edgePercent = 1.0 - (double)((float)islandTileX / 20.0f);
            perlin = this.islandEdgeNoise.perlin2((double)(islandY * 300 + islandTileY) * 0.023333333333333334, (double)(islandY * 300 + islandTileY) * 0.023333333333333334);
            perlinEdgePercent = edgePercent * perlin;
            if (perlin >= 1.0 - edgePercent && perlinEdgePercent > bestEdgePercent) {
                bestEdgePercent = edgePercent * perlin;
                bestEdgeBiome = this.islandBiomes.get(islandX - 1, islandY);
            }
        }
        if (islandTileX >= 280) {
            edgePercent = 1.0 - (double)((float)(300 - islandTileX) / 20.0f);
            perlin = this.islandEdgeNoise.perlin2((double)(islandY * 300 + islandTileY) * 0.023333333333333334, (double)(islandY * 300 + islandTileY) * 0.023333333333333334);
            perlinEdgePercent = edgePercent * -perlin;
            if (-perlin >= 1.0 - edgePercent && perlinEdgePercent > bestEdgePercent) {
                bestEdgePercent = edgePercent * perlin;
                bestEdgeBiome = this.islandBiomes.get(islandX + 1, islandY);
            }
        }
        if (islandTileY <= 20) {
            edgePercent = 1.0 - (double)((float)islandTileY / 20.0f);
            perlin = this.islandEdgeNoise.perlin2((double)(islandX * 300 + islandTileX) * 0.023333333333333334, (double)(islandX * 300 + islandTileX) * 0.023333333333333334);
            perlinEdgePercent = edgePercent * perlin;
            if (perlin >= 1.0 - edgePercent && perlinEdgePercent > bestEdgePercent) {
                bestEdgePercent = edgePercent * perlin;
                bestEdgeBiome = this.islandBiomes.get(islandX, islandY - 1);
            }
        }
        if (islandTileY >= 280) {
            edgePercent = 1.0 - (double)((float)(300 - islandTileY) / 20.0f);
            perlin = this.islandEdgeNoise.perlin2((double)(islandX * 300 + islandTileX) * 0.023333333333333334, (double)(islandX * 300 + islandTileX) * 0.023333333333333334);
            perlinEdgePercent = edgePercent * -perlin;
            if (-perlin >= 1.0 - edgePercent && perlinEdgePercent > bestEdgePercent) {
                bestEdgePercent = edgePercent * perlin;
                bestEdgeBiome = this.islandBiomes.get(islandX, islandY + 1);
            }
        }
        if (bestEdgeBiome == null) {
            int tileX = islandX * 300 + islandTileX;
            int tileY = islandY * 300 + islandTileY;
            bestEdgeBiome = BiomeRegistry.getBiome(this.generatorStack.getExpensiveBiomeID(tileX, tileY));
        }
        return bestEdgeBiome;
    }

    protected static class LevelFile {
        public final WorldFile file;
        public final LoadData save;
        public final LevelIdentifier levelIdentifier;
        public final Biome biome;

        public LevelFile(WorldFile file, LoadData save) {
            this.file = file;
            this.save = save;
            this.levelIdentifier = LevelSave.getLevelSaveIdentifier(save);
            Biome biome = LevelSave.getLevelSaveBiome(save);
            if (biome == null) {
                biome = BiomeRegistry.FOREST;
            }
            this.biome = biome;
        }
    }

    protected static class NewLevelOptions {
        public final LevelIdentifier newLevelIdentifier;
        public final boolean keepOldLevel;

        public NewLevelOptions(LevelIdentifier newLevelIdentifier, boolean keepOldLevel) {
            this.newLevelIdentifier = newLevelIdentifier;
            this.keepOldLevel = keepOldLevel;
        }
    }
}

