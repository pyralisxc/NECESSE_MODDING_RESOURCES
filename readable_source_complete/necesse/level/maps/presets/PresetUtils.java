/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlacePreset;
import necesse.engine.network.packet.PacketRedoClientPreset;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.packet.PacketUndoClientPreset;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.presets.CarpetSelectionTable;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUndoData;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPosition;

public class PresetUtils {
    public static int MAX_PRESET_UNDO_HISTORY = 100;

    public static Preset randomizeRotationAndMirror(Preset preset, GameRandom random) {
        preset = PresetUtils.randomizeXMirror(preset, random);
        preset = PresetUtils.randomizeYMirror(preset, random);
        preset = PresetUtils.randomizeRotation(preset, random);
        return preset;
    }

    public static Preset randomizeXMirror(Preset preset, GameRandom random) {
        if (random.nextBoolean()) {
            return preset.tryMirrorX();
        }
        return preset;
    }

    public static Preset randomizeYMirror(Preset preset, GameRandom random) {
        if (random.nextBoolean()) {
            return preset.tryMirrorY();
        }
        return preset;
    }

    public static Preset randomizeRotation(Preset preset, GameRandom random) {
        return preset.tryRotate(random.getOneOf(PresetRotation.CLOCKWISE, PresetRotation.ANTI_CLOCKWISE, PresetRotation.HALF_180, null));
    }

    public static Point getRotatedPoint(int x, int y, int pivotX, int pivotY, PresetRotation angle) {
        if (angle == PresetRotation.CLOCKWISE) {
            int deltaX = x - pivotX;
            int deltaY = y - pivotY;
            return new Point(pivotX - deltaY, pivotY + deltaX);
        }
        if (angle == PresetRotation.HALF_180) {
            int deltaX = x - pivotX;
            int deltaY = y - pivotY;
            return new Point(pivotX - deltaX, pivotY - deltaY);
        }
        if (angle == PresetRotation.ANTI_CLOCKWISE) {
            int deltaX = x - pivotX;
            int deltaY = y - pivotY;
            return new Point(pivotX + deltaY, pivotY - deltaX);
        }
        return new Point(x, y);
    }

    public static Point getRotatedPointInSpace(int x, int y, int width, int height, PresetRotation angle) {
        Point rp = PresetUtils.getRotatedPoint(x, y, 0, 0, angle);
        if (angle == PresetRotation.CLOCKWISE) {
            rp.x += height - 1;
        } else if (angle == PresetRotation.HALF_180) {
            rp.x += width - 1;
            rp.y += height - 1;
        } else if (angle == PresetRotation.ANTI_CLOCKWISE) {
            rp.y += width - 1;
        }
        return rp;
    }

    public static Point getRotatedPoint(int x, int y, int pivotX, int pivotY, int rightAngles) {
        return PresetUtils.getRotatedPoint(x, y, pivotX, pivotY, PresetRotation.toRotationAngle(rightAngles));
    }

    public static Point[] getRotatedPoints(int pivotX, int pivotY, PresetRotation angle, Point ... points) {
        Point[] out = new Point[points.length];
        for (int i = 0; i < points.length; ++i) {
            Point point = points[i];
            out[i] = PresetUtils.getRotatedPoint(point.x, point.y, pivotX, pivotY, angle);
        }
        return out;
    }

    public static Point[] getRotatedPoints(int pivotX, int pivotY, int rightAngles, Point ... points) {
        return PresetUtils.getRotatedPoints(pivotX, pivotY, PresetRotation.toRotationAngle(rightAngles), points);
    }

    public static int getMirroredValue(int value, int size) {
        return size - 1 - value;
    }

    public static Point getMirroredPoint(int x, int y, boolean xMirror, boolean yMirror, int width, int height) {
        if (xMirror) {
            x = PresetUtils.getMirroredValue(x, width);
        }
        if (yMirror) {
            y = PresetUtils.getMirroredValue(y, height);
        }
        return new Point(x, y);
    }

    public static int getRandomSeed(GameRandom random) {
        return (Integer)random.getOneOf(() -> ObjectRegistry.getObjectID("sunflowerseed"), () -> ObjectRegistry.getObjectID("firemoneseed"), () -> ObjectRegistry.getObjectID("iceblossomseed"), () -> ObjectRegistry.getObjectID("mushroom"), () -> ObjectRegistry.getObjectID("wheatseed"), () -> ObjectRegistry.getObjectID("cornseed"), () -> ObjectRegistry.getObjectID("tomatoseed"), () -> ObjectRegistry.getObjectID("chilipepperseed"), () -> ObjectRegistry.getObjectID("sugarbeetseed"), () -> ObjectRegistry.getObjectID("beetseed"));
    }

    public static void applyRandomSeedArea(Preset preset, int tileX, int tileY, int width, int height, GameRandom random) {
        preset.addCustomApplyRect(tileX, tileY, width, height, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir, blackboard) -> {
            int seed = PresetUtils.getRandomSeed(random);
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    level.setObject(x, y, seed);
                }
            }
            return null;
        });
    }

    public static int getRandomFlower(GameRandom random) {
        return (Integer)random.getOneOf(() -> ObjectRegistry.getObjectID("sunflower"), () -> ObjectRegistry.getObjectID("firemone"), () -> ObjectRegistry.getObjectID("iceblossom"), () -> ObjectRegistry.getObjectID("mushroomflower"));
    }

    public static void applyRandomFlower(Preset preset, int tileX, int tileY, GameRandom random) {
        preset.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            int flower = PresetUtils.getRandomFlower(random);
            level.setObject(levelX, levelY, flower, dir);
            return null;
        });
    }

    public static int getRandomPot(GameRandom random) {
        return (Integer)random.getOneOf(() -> ObjectRegistry.getObjectID("decorativepot1"), () -> ObjectRegistry.getObjectID("decorativepot2"), () -> ObjectRegistry.getObjectID("decorativepot3"), () -> ObjectRegistry.getObjectID("decorativepot4"), () -> ObjectRegistry.getObjectID("pottedcactus1"), () -> ObjectRegistry.getObjectID("pottedcactus2"), () -> ObjectRegistry.getObjectID("pottedcactus3"), () -> ObjectRegistry.getObjectID("pottedcactus4"), () -> ObjectRegistry.getObjectID("pottedflower1"), () -> ObjectRegistry.getObjectID("pottedflower2"), () -> ObjectRegistry.getObjectID("pottedflower3"), () -> ObjectRegistry.getObjectID("pottedflower4"), () -> ObjectRegistry.getObjectID("pottedflower5"), () -> ObjectRegistry.getObjectID("pottedflower6"), () -> ObjectRegistry.getObjectID("pottedplant1"), () -> ObjectRegistry.getObjectID("pottedplant2"), () -> ObjectRegistry.getObjectID("pottedplant3"), () -> ObjectRegistry.getObjectID("pottedplant4"), () -> ObjectRegistry.getObjectID("pottedplant5"), () -> ObjectRegistry.getObjectID("pottedplant6"), () -> ObjectRegistry.getObjectID("pottedplant7"));
    }

    public static void applyRandomPot(Preset preset, int tileX, int tileY, GameRandom random) {
        preset.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            GameObject object = ObjectRegistry.getObject(PresetUtils.getRandomPot(random));
            object.placeObjectOnFirstValidLayer(level, levelX, levelY, dir, false, true);
            return null;
        });
    }

    public static void applyRandomPainting(Preset preset, int tileX, int tileY, int rotation, GameRandom random, HashMap<String, Integer> paintings) {
        String paintingID = PaintingSelectionTable.getRandomPaintingIDFromHashMapBasedOnWeight(paintings, random);
        GameObject object = ObjectRegistry.getObject(paintingID);
        preset.addCustomApply(new CustomApplyTileObject(object, tileX, tileY, rotation, (level, levelX, levelY, dir, blackboard) -> {
            object.placeObjectOnFirstValidLayer(level, levelX, levelY, dir, false, true);
            return null;
        }, true));
    }

    public static void applyRandomCarpetToSelection(Preset preset, int startX, int startY, int width, int height, int rotation, GameRandom random) {
        PresetUtils.applyRandomCarpetToSelection(preset, startX, startY, width, height, rotation, random, null, null);
    }

    public static void applyRandomCarpetToSelection(Preset preset, int startX, int startY, int width, int height, int rotation, GameRandom random, HashMap<String, Integer> specificPaintings) {
        PresetUtils.applyRandomCarpetToSelection(preset, startX, startY, width, height, rotation, random, null, specificPaintings);
    }

    public static void applyRandomCarpetToSelection(Preset preset, int startX, int startY, int width, int height, int rotation, GameRandom random, String carpetIdentifier) {
        PresetUtils.applyRandomCarpetToSelection(preset, startX, startY, width, height, rotation, random, carpetIdentifier, null);
    }

    public static void applyRandomCarpetToSelection(Preset preset, int startX, int startY, int width, int height, int rotation, GameRandom random, String carpetIdentifier, HashMap<String, Integer> specificPaintings) {
        preset.addCustomApplyRect(startX, startY, width, height, rotation, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir, blackboard) -> {
            String randomCarpetID;
            if (carpetIdentifier != null) {
                randomCarpetID = blackboard.getString(carpetIdentifier, null);
                if (randomCarpetID == null) {
                    if (specificPaintings != null) {
                        randomCarpetID = CarpetSelectionTable.getRandomCarpetIDFromSpecificHashmap(random, specificPaintings);
                        blackboard.set(carpetIdentifier, randomCarpetID);
                    } else {
                        randomCarpetID = CarpetSelectionTable.getRandomCarpetID(random);
                        blackboard.set(carpetIdentifier, randomCarpetID);
                    }
                }
            } else {
                randomCarpetID = specificPaintings != null ? CarpetSelectionTable.getRandomCarpetIDFromSpecificHashmap(random, specificPaintings) : CarpetSelectionTable.getRandomCarpetID(random);
            }
            GameObject object = ObjectRegistry.getObject(randomCarpetID);
            for (int levelX = levelStartX; levelX <= levelEndX; ++levelX) {
                for (int levelY = levelStartY; levelY <= levelEndY; ++levelY) {
                    object.placeObjectOnFirstValidLayer(level, levelX, levelY, dir, false, true);
                }
            }
            return null;
        });
    }

    public static Function<GameRandom, String> getRandomHusbandryMobGetter(GameRandom random) {
        return (Function)random.getOneOf(() -> r -> {
            ProtectedTicketSystemList cowLottery = ((TicketSystemList)new TicketSystemList().addObject(100, "cow")).addObject(20, "bull");
            return (String)((TicketSystemList)cowLottery).getRandomObject((Random)r);
        }, () -> r -> {
            ProtectedTicketSystemList sheepLottery = ((TicketSystemList)new TicketSystemList().addObject(100, "sheep")).addObject(20, "ram");
            return (String)((TicketSystemList)sheepLottery).getRandomObject((Random)r);
        }, () -> r -> {
            ProtectedTicketSystemList chickenLottery = ((TicketSystemList)new TicketSystemList().addObject(100, "chicken")).addObject(20, "rooster");
            return (String)((TicketSystemList)chickenLottery).getRandomObject((Random)r);
        });
    }

    public static void addDeterioration(Preset preset, GameRandom random, int deteriorateEveryXth) {
        for (int i = 0; i < preset.width; ++i) {
            for (int j = 0; j < preset.height; ++j) {
                if (random.getEveryXthChance(deteriorateEveryXth)) {
                    preset.setTile(i, j, -1);
                    preset.setObject(i, j, 0);
                    continue;
                }
                int objectID = preset.getObject(i, j);
                if (objectID == -1) continue;
                GameObject object = ObjectRegistry.getObject(objectID);
                if (!object.isSeed) continue;
                preset.setObject(i, j, 0);
            }
        }
    }

    public static void addFuelToInventory(Preset preset, int tileX, int tileY, GameRandom random, String itemStringID, int minItems, int maxItems, boolean ensureKeepRunning) {
        if (itemStringID == null) {
            itemStringID = "oaklog";
        }
        InventoryItem item = new InventoryItem(itemStringID, random.getIntBetween(minItems, maxItems));
        preset.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(levelX, levelY);
            if (objectEntity instanceof FueledInventoryObjectEntity) {
                FueledInventoryObjectEntity fueledInventory = (FueledInventoryObjectEntity)objectEntity;
                if (fueledInventory.inventory.getSize() > 0) {
                    fueledInventory.inventory.setItem(0, item);
                }
                if (ensureKeepRunning) {
                    fueledInventory.keepRunning = true;
                }
            } else if (objectEntity instanceof FueledProcessingInventoryObjectEntity) {
                FueledProcessingInventoryObjectEntity fueledInventory = (FueledProcessingInventoryObjectEntity)objectEntity;
                if (fueledInventory.fuelSlots > 0) {
                    fueledInventory.inventory.setItem(0, item);
                }
                if (ensureKeepRunning) {
                    fueledInventory.setKeepFuelRunning(true);
                }
            }
            return null;
        });
    }

    public static void addShoreTiles(Preset preset, int tileX, int tileY, int width, int height, final Function<LevelIdentifier, Function<Biome, Integer>> identifierToBiomeToTileIDGetter) {
        preset.addCustomPreApplyRect(tileX, tileY, width, height, 0, new Preset.CustomApplyAreaFunction(){

            @Override
            public Preset.UndoLogic applyToLevel(Level level, int levelStartX, int levelStartY, int levelEndX, int levelEndY, int dir, GameBlackboard blackboard) {
                Function biomeToTileID = (Function)identifierToBiomeToTileIDGetter.apply(level.getIdentifier());
                for (int levelX = levelStartX; levelX <= levelEndX; ++levelX) {
                    for (int levelY = levelStartY; levelY <= levelEndY; ++levelY) {
                        if (!level.getTile((int)levelX, (int)levelY).isLiquid) continue;
                        Biome biome = level.getBiome(levelX, levelY);
                        level.setTile(levelX, levelY, (Integer)biomeToTileID.apply(biome));
                    }
                }
                return null;
            }
        });
    }

    public static void addShoreTiles(Preset preset, int tileX, int tileY, int width, int height) {
        PresetUtils.addShoreTiles(preset, tileX, tileY, width, height, identifier -> {
            if (identifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
                return Biome::getGenerationCaveTileID;
            }
            if (identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return Biome::getGenerationDeepCaveTileID;
            }
            return biome -> {
                if (biome.doesGenerationPreventsBeachTiles()) {
                    return biome.getGenerationTerrainTileID();
                }
                return biome.getGenerationBeachTileID();
            };
        });
    }

    public static void clearMobsInPreset(Preset preset, Level level, int placeTileX, int placeTileY) {
        PresetUtils.clearMobsTilRectangle(level, new Rectangle(placeTileX, placeTileY, preset.width, preset.height));
    }

    public static void clearMobsTilRectangle(Level level, Rectangle tileRectangle) {
        Rectangle shape = new Rectangle(tileRectangle.x * 32, tileRectangle.y * 32, tileRectangle.width * 32, tileRectangle.height * 32);
        level.entityManager.mobs.streamInRegionsShape(shape, 1).filter(m -> shape.intersects(m.getCollision())).forEach(Mob::remove);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void placePresetFromClient(Client client, Preset preset, int tileX, int tileY) {
        int nextUniqueID;
        Level level = client.getLevel();
        if (level == null) {
            return;
        }
        do {
            nextUniqueID = GameRandom.globalRandom.nextInt();
        } while (!client.presetUndoData.stream().noneMatch(data -> data.uniqueID == nextUniqueID) || client.presetRedoUniqueIDs.contains(nextUniqueID));
        int undoUniqueID = nextUniqueID;
        Preset undoPreset = new Preset(preset.width, preset.height);
        undoPreset.copyFromLevel(level, tileX, tileY);
        PresetUndoData undoData = new PresetUndoData(undoUniqueID, level.getIdentifier(), undoPreset, tileX, tileY);
        undoData.clientUndos = preset.applyToLevel(level, tileX, tileY);
        client.presetUndoData.add(undoData);
        while (client.presetUndoData.size() > MAX_PRESET_UNDO_HISTORY) {
            client.presetUndoData.remove(0);
        }
        client.presetRedoUniqueIDs.clear();
        Server server = client.getLocalServer();
        try {
            Level serverLevel;
            PacketPlacePreset placePresetPacket = new PacketPlacePreset(level, undoUniqueID, tileX, tileY, preset);
            if (server != null && (serverLevel = server.world.getLevel(level.getIdentifier())) != null) {
                Object object = serverLevel.entityManager.lock;
                synchronized (object) {
                    LightManager lightManager = serverLevel.lightManager;
                    synchronized (lightManager) {
                        Preset serverUndoPreset = new Preset(preset.width, preset.height);
                        serverUndoPreset.clearOtherWires = true;
                        serverUndoPreset.copyFromLevel(serverLevel, tileX, tileY);
                        undoData.serverPreset = serverUndoPreset;
                        undoData.serverUndos = preset.applyToLevel(serverLevel, tileX, tileY);
                        LinkedList<RegionPosition> regionPositions = PresetUtils.getRegionPositions(level, preset, tileX, tileY);
                        server.network.sendToClientsWithAnyRegionExcept(placePresetPacket, regionPositions, server.getLocalServerClient());
                    }
                }
                return;
            }
            client.network.sendPacket(placePresetPacket);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void placePresetFromServer(ServerClient client, Preset preset, Level level, int tileX, int tileY, int undoUniqueID, boolean clearRedoHistory) {
        Preset undoPreset = new Preset(preset.width, preset.height);
        undoPreset.copyFromLevel(level, tileX, tileY);
        undoPreset.clearOtherWires = true;
        PresetUndoData undoData = new PresetUndoData(undoUniqueID, level.getIdentifier(), null, tileX, tileY);
        undoData.serverPreset = undoPreset;
        undoData.serverUndos = preset.applyToLevel(level, tileX, tileY);
        client.presetUndoData.add(undoData);
        while (client.presetUndoData.size() > MAX_PRESET_UNDO_HISTORY) {
            client.presetUndoData.remove(0);
        }
        if (clearRedoHistory) {
            client.presetRedoData.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static GameMessage undoLatestPresetFromClient(Client client) {
        if (!client.presetUndoData.isEmpty()) {
            Server server;
            PresetUndoData latestPreset = client.presetUndoData.remove(client.presetUndoData.size() - 1);
            if (latestPreset.clientPreset == null) {
                client.network.sendPacket(new PacketUndoClientPreset(latestPreset.uniqueID));
                return null;
            }
            int tileX = latestPreset.tileX;
            int tileY = latestPreset.tileY;
            Level clientLevel = client.getLevel();
            if (clientLevel.getIdentifier().equals(latestPreset.levelIdentifier)) {
                latestPreset.applyClient(clientLevel);
            }
            client.presetRedoUniqueIDs.add(latestPreset.uniqueID);
            if (latestPreset.serverPreset != null && (server = client.getLocalServer()) != null) {
                ServerClient serverClient = server.getLocalServerClient();
                Level serverLevel = server.world.getLevel(latestPreset.levelIdentifier);
                if (serverClient != null && serverLevel != null) {
                    Object object = serverLevel.entityManager.lock;
                    synchronized (object) {
                        LightManager lightManager = serverLevel.lightManager;
                        synchronized (lightManager) {
                            latestPreset.applyServer(serverLevel, serverClient.presetRedoData);
                            LinkedList<RegionPosition> regionPositions = PresetUtils.getRegionPositions(serverLevel, latestPreset.serverPreset, latestPreset.tileX, latestPreset.tileY);
                            try {
                                PacketPlacePreset placePacket = new PacketPlacePreset(serverLevel, latestPreset.uniqueID, tileX, tileY, latestPreset.serverPreset);
                                server.network.sendToClientsWithAnyRegionExcept(placePacket, regionPositions, serverClient);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return null;
            }
            client.network.sendPacket(new PacketUndoClientPreset(latestPreset.uniqueID, client.getLevel().getIdentifier(), latestPreset.clientPreset, tileX, tileY));
            return null;
        }
        return new LocalMessage("ui", "presetnoundohistory");
    }

    public static GameMessage redoLatestPreset(Client client) {
        if (!client.presetRedoUniqueIDs.isEmpty()) {
            int latestPresetUniqueID = client.presetRedoUniqueIDs.remove(client.presetRedoUniqueIDs.size() - 1);
            client.network.sendPacket(new PacketRedoClientPreset(latestPresetUniqueID));
            return null;
        }
        return new LocalMessage("ui", "presetnoredohistory");
    }

    protected static boolean clientHasAnyRegionLoaded(Client client, int startTileX, int startTileY, int endTileX, int endTileY) {
        Level level = client.getLevel();
        int startRegionX = level.regionManager.getRegionXByTileLimited(startTileX);
        int startRegionY = level.regionManager.getRegionYByTileLimited(startTileY);
        int endRegionX = level.regionManager.getRegionXByTileLimited(endTileX);
        int endRegionY = level.regionManager.getRegionYByTileLimited(endTileY);
        for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
            for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                if (!client.levelManager.isRegionLoaded(regionX, regionY)) continue;
                return true;
            }
        }
        return false;
    }

    public static LinkedList<Preset.UndoLogic> applyPresetIfClientHasRegionsLoaded(Client client, int levelIdentifierHashCode, Preset preset, int tileX, int tileY) {
        if (!client.levelManager.isLevelLoaded(levelIdentifierHashCode)) {
            return null;
        }
        if (PresetUtils.clientHasAnyRegionLoaded(client, tileX, tileY, tileX + preset.width - 1, tileY + preset.height - 1)) {
            return preset.applyToLevel(client.getLevel(), tileX, tileY);
        }
        return null;
    }

    public static LinkedList<RegionPosition> getRegionPositions(Level level, int startTileX, int startTileY, int endTileX, int endTileY) {
        int startRegionX = level.regionManager.getRegionXByTileLimited(startTileX);
        int startRegionY = level.regionManager.getRegionYByTileLimited(startTileY);
        int endRegionX = level.regionManager.getRegionXByTileLimited(endTileX);
        int endRegionY = level.regionManager.getRegionYByTileLimited(endTileY);
        LinkedList<RegionPosition> regionPositions = new LinkedList<RegionPosition>();
        for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
            for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                regionPositions.add(new RegionPosition(level, regionX, regionY));
            }
        }
        return regionPositions;
    }

    public static LinkedList<RegionPosition> getRegionPositions(Level level, Rectangle tileBounds) {
        return PresetUtils.getRegionPositions(level, tileBounds.x, tileBounds.y, tileBounds.x + tileBounds.width - 1, tileBounds.y + tileBounds.height - 1);
    }

    public static LinkedList<RegionPosition> getRegionPositions(Level level, Preset preset, int tileX, int tileY) {
        return PresetUtils.getRegionPositions(level, tileX, tileY, tileX + preset.width - 1, tileY + preset.height - 1);
    }

    public static void updateClientLevels(Server server, Level level, int startTileX, int startTileY, int endTileX, int endTileY) {
        for (RegionPosition regionPosition : PresetUtils.getRegionPositions(level, startTileX, startTileY, endTileX, endTileY)) {
            Region region = level.regionManager.getRegion(regionPosition.regionX, regionPosition.regionY, false);
            if (region == null) {
                return;
            }
            PacketRegionData packet = new PacketRegionData(region);
            server.network.sendToClientsWithRegion((Packet)packet, level, regionPosition.regionX, regionPosition.regionY);
        }
    }

    public static void placeAndSendPresetToClients(Server server, Preset preset, Level level, int tileX, int tileY) {
        try {
            preset.applyToLevel(level, tileX, tileY);
            LinkedList<RegionPosition> regionPositions = PresetUtils.getRegionPositions(level, preset, tileX, tileY);
            server.network.sendToClientsWithAnyRegion(new PacketPlacePreset(level, 0, tileX, tileY, preset), regionPositions);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class CustomApplyTileObject
    extends Preset.CustomApplyTile {
        public final GameObject object;

        public CustomApplyTileObject(GameObject object, int tileX, int tileY, int dir, Preset.CustomApplyFunction applyFunction, boolean limitWithinBounds) {
            super(tileX, tileY, dir, applyFunction, limitWithinBounds);
            this.object = object;
        }

        @Override
        public Preset.CustomApply mirrorX(int width) throws PresetMirrorException {
            MultiTile multiTile = this.object.getMultiTile(this.dir);
            Point offset = multiTile.getMirrorXPosOffset();
            if (offset == null) {
                throw new PresetMirrorException(new LocalMessage("ui", "presetmirrorerror", "item", this.object.getLocalization()));
            }
            int newDir = multiTile.getXMirrorRotation();
            return new CustomApplyTileObject(this.object, PresetUtils.getMirroredValue(this.tileX, width) + offset.x, this.tileY + offset.y, newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public Preset.CustomApply mirrorY(int height) throws PresetMirrorException {
            MultiTile multiTile = this.object.getMultiTile(this.dir);
            Point offset = multiTile.getMirrorYPosOffset();
            if (offset == null) {
                throw new PresetMirrorException(new LocalMessage("ui", "presetmirrorerror", "item", this.object.getLocalization()));
            }
            int newDir = multiTile.getYMirrorRotation();
            return new CustomApplyTileObject(this.object, this.tileX + offset.x, PresetUtils.getMirroredValue(this.tileY, height) + offset.y, newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public Preset.CustomApply rotate(PresetRotation angle, int width, int height) throws PresetRotateException {
            MultiTile multiTile = this.object.getMultiTile(this.dir);
            Point offset = multiTile.getPresetRotationOffset(angle);
            if (offset == null) {
                throw new PresetRotateException(new LocalMessage("ui", "presetrotateerror", "item", this.object.getLocalization()));
            }
            int newDir = multiTile.getPresetRotation(angle);
            Point pos = PresetUtils.getRotatedPointInSpace(this.tileX, this.tileY, width, height, angle);
            return new CustomApplyTileObject(this.object, pos.x + offset.x, pos.y + offset.y, newDir, this.applyFunction, this.limitWithinBounds);
        }
    }
}

