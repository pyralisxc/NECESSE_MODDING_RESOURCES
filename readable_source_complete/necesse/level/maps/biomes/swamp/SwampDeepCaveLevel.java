/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.swamp;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.AreaFinder;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.util.voronoi.DelaunayTriangulator;
import necesse.engine.util.voronoi.TriangleLine;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.swamp.SwampCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.AgedChampionArenaPreset;
import necesse.level.maps.presets.FishianHousePreset1;
import necesse.level.maps.presets.FishianHousePreset2;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.RandomLootAreaPreset;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class SwampDeepCaveLevel
extends SwampCaveLevel {
    public SwampDeepCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SwampDeepCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    @Override
    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "deepswamprocktile", "deepswamprock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel());
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        int crate = ObjectRegistry.getObjectID("swampcrate");
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.02f, 4, 15.0f, 25.0f, 3.0f, 5.0f, ca -> {
                ca.forEachTile(this, (level, tileX, tileY) -> {
                    level.setTile(tileX, tileY, TileRegistry.spiderNestID);
                    if (cg.random.getChance(0.95f)) {
                        level.setObject(tileX, tileY, ObjectRegistry.cobWebID);
                    } else {
                        level.setObject(tileX, tileY, 0);
                    }
                });
                ca.spawnMobs(this, cg.random, "smallswampcavespider", 4, 8, 1, 8);
            });
            GameObject crystalClusterSmall = ObjectRegistry.getObject("emeraldclustersmall");
            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.005f, 4, 3.0f, 5.0f, 4.0f, 6.0f, lg -> {
                CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                ca.streamAliveOrdered().forEachOrdered(tile -> {
                    cg.addIllegalCrateTile(tile.x, tile.y);
                    this.setTile(tile.x, tile.y, TileRegistry.getTileID("emeraldgravel"));
                    this.setObject(tile.x, tile.y, 0);
                });
                ca.streamAliveOrdered().forEachOrdered(tile -> {
                    Point[] clearPoints;
                    int rotation;
                    if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.1f) && this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation = cg.random.nextInt(4), clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && this.getObjectID((int)tileX, (int)tileY) == 0)) {
                        ObjectRegistry.getObject(ObjectRegistry.getObjectID("emeraldcluster")).placeObject(this, tile.x, tile.y, rotation, false);
                    }
                    if (cg.random.getChance(0.3f) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                        crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                    }
                });
            });
            GameTile swampRockTile = TileRegistry.getTile(TileRegistry.deepSwampRockID);
            GameObject tallGrass = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswamptallgrass"));
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.03f, 5, 4.0f, 10.0f, 3.0f, 5.0f, cells -> cells.forEachTile(this, (level, tileX, tileY) -> {
                swampRockTile.placeTile(level, tileX, tileY, false);
                this.setObject(tileX, tileY, 0);
                if (cg.random.getChance(0.85f) && tallGrass.canPlace(level, tileX, tileY, 0, false) == null) {
                    tallGrass.placeObject(level, tileX, tileY, 0, false);
                }
            }));
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.04f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("lavatile"), 1.0f, true);
            int fishianSize = 75;
            int cobbleID = TileRegistry.puddleCobble;
            int edgePadding = 20;
            int centerTileX = cg.random.getIntBetween(edgePadding, this.tileWidth - edgePadding - fishianSize);
            int centerTileY = cg.random.getIntBetween(edgePadding, this.tileHeight - edgePadding - fishianSize);
            HashSet validTiles = new HashSet();
            Rectangle bounds = new Rectangle(centerTileX, centerTileY, centerTileX, centerTileY);
            LinesGeneration lg2 = new LinesGeneration(centerTileX, centerTileY).addRandomArms(cg.random, 16, fishianSize / 2 - 10, fishianSize / 2, 6.0f, 8.0f);
            CellAutomaton ca2 = lg2.doCellularAutomaton(cg.random);
            ca2.forEachTile(this, (level, tileX, tileY) -> {
                if (bounds.x > tileX) {
                    bounds.x = tileX;
                }
                if (bounds.y > tileY) {
                    bounds.y = tileY;
                }
                if (bounds.width < tileX) {
                    bounds.width = tileX;
                }
                if (bounds.height < tileY) {
                    bounds.height = tileY;
                }
                validTiles.add(new Point(tileX, tileY));
                this.setTile(tileX, tileY, cobbleID);
                this.setObject(tileX, tileY, 0);
            });
            bounds.width -= bounds.x;
            bounds.height -= bounds.y;
            presets.addOccupiedSpace(bounds);
            ArrayList<Point2D.Float> voronoiPoints = new ArrayList<Point2D.Float>();
            int voronoiPadding = -20;
            int usableWidth = bounds.width - voronoiPadding * 2;
            int usableHeight = bounds.height - voronoiPadding * 2;
            int resolution = 20;
            int resWidth = usableWidth / resolution;
            int resHeight = usableHeight / resolution;
            int xOffset = bounds.x + voronoiPadding + usableWidth % resolution / 2;
            int yOffset = bounds.y + voronoiPadding + usableHeight % resolution / 2;
            for (int x2 = 0; x2 < resWidth; ++x2) {
                int minX = x2 * resolution + xOffset;
                int maxX = minX + resolution;
                for (int y2 = 0; y2 < resHeight; ++y2) {
                    int minY = y2 * resolution + yOffset;
                    int maxY = minY + resolution;
                    Point2D.Float point = new Point2D.Float(cg.random.getIntBetween(minX, maxX), cg.random.getIntBetween(minY, maxY));
                    voronoiPoints.add(point);
                }
            }
            ArrayList<TriangleLine> voronoiLines = new ArrayList<TriangleLine>();
            DelaunayTriangulator.compute(voronoiPoints, false, voronoiLines);
            HashSet floorTiles = new HashSet();
            HashSet houseTiles = new HashSet();
            int stoneFloor = TileRegistry.deepSwampStoneFloorID;
            for (TriangleLine line : voronoiLines) {
                LinesGeneration.pathTiles(new Line2D.Float(line.p1, line.p2), true, (from, next) -> {
                    int tileX = next.x;
                    int tileY = next.y;
                    if (!this.isTileWithinBounds(tileX, tileY)) {
                        return;
                    }
                    if (!validTiles.contains(new Point(tileX, tileY))) {
                        return;
                    }
                    if (cg.random.getChance(0.8f)) {
                        this.setTile(tileX, tileY, stoneFloor);
                    }
                    this.setObject(tileX, tileY, 0);
                    floorTiles.add(new Point(tileX, tileY));
                });
            }
            AtomicInteger lootRotation = new AtomicInteger();
            ArrayList<Preset> housePresets = new ArrayList<Preset>();
            housePresets.add(new FishianHousePreset1(cg.random, lootRotation));
            housePresets.add(new FishianHousePreset2(cg.random, lootRotation));
            for (Preset housePreset : housePresets) {
                housePreset.addCanApplyRectEachPredicate(0, 0, housePreset.width, housePreset.height, 0, (level, levelX, levelY, dir) -> !floorTiles.contains(new Point(levelX, levelY)) && validTiles.contains(new Point(levelX, levelY)));
                housePreset.addCustomApplyRectEach(-1, -1, housePreset.width + 2, housePreset.height + 2, 0, (level, levelX, levelY, dir, blackboard) -> {
                    houseTiles.add(new Point(levelX, levelY));
                    return null;
                });
            }
            while (!voronoiPoints.isEmpty()) {
                int pointsIndex = cg.random.nextInt(voronoiPoints.size());
                Point2D.Float centerTile = voronoiPoints.remove(pointsIndex);
                if (!housePresets.isEmpty() && bounds.contains(centerTile.x, centerTile.y) && validTiles.contains(new Point((int)centerTile.x, (int)centerTile.y))) {
                    int presetIndex = cg.random.nextInt(housePresets.size());
                    Preset preset = (Preset)housePresets.get(presetIndex);
                    int placeDir = cg.random.nextInt(4);
                    try {
                        preset = preset.rotate(PresetRotation.toRotationAngle(placeDir));
                    }
                    catch (PresetRotateException presetRotateException) {
                        // empty catch block
                    }
                    final Preset finalPreset = preset;
                    AreaFinder posFinder = new AreaFinder((int)centerTile.x, (int)centerTile.y, 5, true){

                        @Override
                        public boolean checkPoint(int x, int y) {
                            return finalPreset.canApplyToLevelCentered(SwampDeepCaveLevel.this, x, y);
                        }
                    };
                    posFinder.runFinder();
                    if (posFinder.hasFound()) {
                        housePresets.remove(presetIndex);
                        Point tile = posFinder.getFirstFind();
                        GameBlackboard blackboard2 = new GameBlackboard();
                        finalPreset.applyToLevelCentered(this, tile.x, tile.y, blackboard2);
                        Point doorTile = blackboard2.get(Point.class, "doorTile");
                        if (doorTile != null) {
                            int angle = 0;
                            switch ((placeDir + 2) % 4) {
                                case 0: {
                                    angle = cg.random.getIntOffset(270, 45);
                                    break;
                                }
                                case 1: {
                                    angle = cg.random.getIntOffset(0, 45);
                                    break;
                                }
                                case 2: {
                                    angle = cg.random.getIntOffset(90, 45);
                                    break;
                                }
                                case 3: {
                                    angle = cg.random.getIntOffset(180, 45);
                                }
                            }
                            Point2D.Float dir2 = GameMath.getAngleDir(angle);
                            LinesGeneration.pathTilesBreak(new Line2D.Float(doorTile.x, doorTile.y, (float)doorTile.x + dir2.x * 30.0f, (float)doorTile.y + dir2.y * 30.0f), true, (from, next) -> {
                                int tileX = next.x;
                                int tileY = next.y;
                                if (!this.isTileWithinBounds(tileX, tileY)) {
                                    return true;
                                }
                                if (floorTiles.contains(new Point(tileX, tileY))) {
                                    return false;
                                }
                                this.setTile(tileX, tileY, stoneFloor);
                                this.setObject(tileX, tileY, 0);
                                floorTiles.add(new Point(tileX, tileY));
                                return true;
                            });
                            continue;
                        }
                    }
                }
                if (floorTiles.contains(new Point((int)centerTile.x, (int)centerTile.y)) || houseTiles.contains(new Point((int)centerTile.x, (int)centerTile.y))) continue;
                LinesGeneration lakeLG = new LinesGeneration((int)centerTile.x, (int)centerTile.y);
                int lakeArms = cg.random.getIntBetween(5, 7);
                int angle = cg.random.nextInt(360);
                int anglePerArm = 360 / lakeArms;
                for (int i = 0; i < lakeArms; ++i) {
                    Point2D.Float lakeDir = GameMath.getAngleDir(angle += cg.random.getIntOffset(anglePerArm, anglePerArm / 2));
                    AtomicReference<Point> armEndPointRef = new AtomicReference<Point>(new Point((int)centerTile.x, (int)centerTile.y));
                    LinesGeneration.pathTilesBreak(new Line2D.Float(centerTile.x, centerTile.y, centerTile.x + lakeDir.x * (float)resolution, centerTile.y + lakeDir.y * (float)resolution), true, (from, next) -> {
                        int tileX = next.x;
                        int tileY = next.y;
                        if (!this.isTileWithinBounds(tileX, tileY)) {
                            return false;
                        }
                        if (floorTiles.contains(new Point(tileX, tileY))) {
                            return false;
                        }
                        if (houseTiles.contains(new Point(tileX, tileY))) {
                            return false;
                        }
                        armEndPointRef.set(new Point(tileX, tileY));
                        return true;
                    });
                    Point armEndPoint = armEndPointRef.get();
                    float width = cg.random.getFloatBetween(2.0f, 3.0f);
                    lakeLG.addLine((int)centerTile.x, (int)centerTile.y, armEndPoint.x, armEndPoint.y, width);
                }
                GameObject cattail = ObjectRegistry.getObject("cattail");
                GameObject reeds = ObjectRegistry.getObject("reeds");
                CellAutomaton lakeCA = lakeLG.doCellularAutomaton(cg.random);
                lakeCA.forEachTile(this, (level, tileX, tileY) -> {
                    if (!validTiles.contains(new Point(tileX, tileY))) {
                        return;
                    }
                    if (floorTiles.contains(new Point(tileX, tileY))) {
                        return;
                    }
                    if (houseTiles.contains(new Point(tileX, tileY))) {
                        return;
                    }
                    this.setTile(tileX, tileY, TileRegistry.waterID);
                    this.setObject(tileX, tileY, 0);
                    if (cg.random.getChance(0.1f) && reeds.canPlace(this, 0, tileX, tileY, 0, false, false) == null) {
                        reeds.placeObject(this, 0, tileX, tileY, 0, false);
                    }
                    if (cg.random.getChance(0.1f) && cattail.canPlace(this, 0, tileX, tileY, 0, false, false) == null) {
                        cattail.placeObject(this, 0, tileX, tileY, 0, false);
                    }
                });
                for (Point floorTile : floorTiles) {
                    for (int x3 = -1; x3 <= 1; ++x3) {
                        int tileX2 = floorTile.x + x3;
                        for (int y3 = -1; y3 <= 1; ++y3) {
                            int tileY2 = floorTile.y + y3;
                            Point tile = new Point(tileX2, tileY2);
                            if (floorTiles.contains(tile) || houseTiles.contains(tile)) continue;
                            this.setTile(tileX2, tileY2, cobbleID);
                        }
                    }
                }
                TicketSystemList randomObjects = new TicketSystemList();
                randomObjects.addObject(20, ObjectRegistry.getObject("seashell"));
                randomObjects.addObject(20, ObjectRegistry.getObject("seasnail"));
                randomObjects.addObject(20, ObjectRegistry.getObject("seastar"));
                randomObjects.addObject(25, ObjectRegistry.getObject("bamboodebris"));
                randomObjects.addObject(25, ObjectRegistry.getObject("bambootree"));
                randomObjects.addObject(100, ObjectRegistry.getObject("glowcoral"));
                for (Point tile : validTiles) {
                    GameObject obj;
                    if (floorTiles.contains(tile) || !cg.random.getChance(0.01f) || (obj = (GameObject)randomObjects.getRandomObject(cg.random)).canPlace(this, 0, tile.x, tile.y, 0, false, true) != null) continue;
                    obj.placeObject(this, 0, tile.x, tile.y, 0, false);
                }
            }
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverocksmall"), 0.01f);
            GameObject grassObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswampgrass"));
            GenerationTools.iterateLevel(this, (x, y) -> this.getTileID((int)x, (int)y) == TileRegistry.deepSwampRockID && this.getObjectID((int)x, (int)y) == 0 && cg.random.getChance(0.6f), (x, y) -> grassObject.placeObject(this, (int)x, (int)y, 0, false));
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("copperoredeepswamprock"));
            cg.generateOreVeins(0.1f, 3, 6, ObjectRegistry.getObjectID("ironoredeepswamprock"));
            cg.generateOreVeins(0.15f, 3, 6, ObjectRegistry.getObjectID("goldoredeepswamprock"));
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("tungstenoredeepswamprock"));
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("lifequartzdeepswamprock"));
            cg.generateOreVeins(0.17f, 3, 6, ObjectRegistry.getObjectID("myceliumoredeepswamprock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            this.preGeneratedStructures(cg, presets);
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.deepSwampCaveChest, chestRoomRotation, ChestRoomSet.deepSwampStone, ChestRoomSet.deepStone);
                chestRoom.replaceTile(TileRegistry.deepStoneFloorID, cg.random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
                chestRoom.replaceTile(TileRegistry.deepSwampStoneFloorID, cg.random.getOneOf(TileRegistry.deepSwampStoneFloorID, TileRegistry.deepSwampStoneBrickFloorID));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }
            int lootAreaAmount = cg.random.getIntBetween(5, 10);
            for (int i = 0; i < lootAreaAmount; ++i) {
                RandomLootAreaPreset lootArea = new RandomLootAreaPreset(cg.random, 15, "deepswampstonecolumn", "swampdweller");
                presets.findRandomValidPositionAndApply(cg.random, 5, lootArea, 10, true, true);
            }
            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);
            for (int i = 0; i < caveRuinsCount; ++i) {
                WallSet wallSet = cg.random.getOneOf(WallSet.deepSwampStone, WallSet.deepStone);
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
                String floorStringID = cg.random.getOneOf("deepswampstonefloor", "deepswampstonebrickfloor");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.swampDeepCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            presets.findRandomValidPositionAndApply(cg.random, 200, new AgedChampionArenaPreset(), 2, true, true);
            cg.generateRandomCrates(0.03f, crate);
            this.postGeneratedStructures(cg, presets);
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.swampDeepCrate;
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "deepcave", "biome", this.getBiome(tileX, tileY).getLocalization());
    }
}

