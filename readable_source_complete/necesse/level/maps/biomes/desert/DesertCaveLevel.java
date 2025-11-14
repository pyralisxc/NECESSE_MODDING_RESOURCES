/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.desert;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.desert.DesertSurfaceLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.generationModules.RiverGeneration;
import necesse.level.maps.presets.AncientVultureArenaPreset;
import necesse.level.maps.presets.CavelingOasisPreset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class DesertCaveLevel
extends DesertSurfaceLevel {
    public DesertCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DesertCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "sandstonetile", "sandstonerock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel());
        GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("lavatile"), 1.0f, true);
        GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("watertile"), 1.0f, true);
        int quicksandID = TileRegistry.getTileID("quicksandtile");
        GenerationTools.generateRandomSmoothVeins(this, cg.random, 0.03f, 5, 8.0f, 15.0f, 3.0f, 7.0f, (l, tileX, tileY) -> {
            this.setTile(tileX, tileY, quicksandID);
            this.setObject(tileX, tileY, 0);
        });
        int riverCount = cg.random.getIntBetween(4, 6);
        for (int i = 0; i < riverCount; ++i) {
            RiverGeneration.generateOneSmallRiver(cg.random, this, null, r -> r.getOneOf(RiverGeneration.WOOD_BRIDGE, RiverGeneration.SAND_BRIDGE));
        }
        this.liquidManager.calculateShores();
        int crate = ObjectRegistry.getObjectID("crate");
        int vase = ObjectRegistry.getObjectID("vase");
        int trackObject = ObjectRegistry.getObjectID("minecarttrack");
        LinkedList minecartsGenerated = new LinkedList();
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomPoints(this, cg.random, 0.01f, 15, p -> {
                LinesGeneration lg = new LinesGeneration(p.x, p.y);
                ArrayList<LinesGeneration> tntArms = new ArrayList<LinesGeneration>();
                int armAngle = cg.random.nextInt(4) * 90;
                int arms = cg.random.getIntBetween(3, 10);
                for (int i = 0; i < arms; ++i) {
                    lg = lg.addArm(cg.random.getIntOffset(armAngle, 20), cg.random.getIntBetween(5, 25), 3.0f);
                    tntArms.add(lg);
                    int angleChange = cg.random.getOneOfWeighted(Integer.class, 15, 0, 5, 90, 5, -90);
                    armAngle += angleChange;
                }
                CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                ca.forEachTile(this, (level, tileX, tileY) -> {
                    if (level.isSolidTile(tileX, tileY)) {
                        level.setObject(tileX, tileY, 0);
                    }
                    if (cg.random.getChance(0.05)) {
                        level.setObject(tileX, tileY, crate);
                    }
                });
                ArrayList trackTiles = new ArrayList();
                lg.getRoot().recursiveLines(lg2 -> {
                    GameLinkedList tiles = new GameLinkedList();
                    LinesGeneration.pathTiles(lg2.getTileLine(), true, (from, next) -> tiles.add(next));
                    for (GameLinkedList.Element el : tiles.elements()) {
                        Point current = (Point)el.object;
                        if (!this.isTileWithinBounds(current.x, current.y, 2)) continue;
                        trackTiles.add(current);
                        GameLinkedList.Element nextEl = el.next();
                        if (nextEl != null) {
                            Point next2 = (Point)nextEl.object;
                            if (next2.x < current.x) {
                                this.setObject(current.x, current.y, trackObject, 3);
                                continue;
                            }
                            if (next2.x > current.x) {
                                this.setObject(current.x, current.y, trackObject, 1);
                                continue;
                            }
                            if (next2.y < current.y) {
                                this.setObject(current.x, current.y, trackObject, 0);
                                continue;
                            }
                            if (next2.y <= current.y) continue;
                            this.setObject(current.x, current.y, trackObject, 2);
                            continue;
                        }
                        GameLinkedList.Element prevEl = el.prev();
                        if (prevEl != null) {
                            Point prev = (Point)prevEl.object;
                            if (prev.x < current.x) {
                                this.setObject(current.x, current.y, trackObject, 1);
                                continue;
                            }
                            if (prev.x > current.x) {
                                this.setObject(current.x, current.y, trackObject, 3);
                                continue;
                            }
                            if (prev.y < current.y) {
                                this.setObject(current.x, current.y, trackObject, 2);
                                continue;
                            }
                            if (prev.y <= current.y) continue;
                            this.setObject(current.x, current.y, trackObject, 0);
                            continue;
                        }
                        this.setObject(current.x, current.y, trackObject, 0);
                    }
                    return true;
                });
                int minecartCount = cg.random.getOneOfWeighted(Integer.class, 100, 0, 200, 1, 50, 2);
                for (int i = 0; i < minecartCount && !trackTiles.isEmpty(); ++i) {
                    int index = cg.random.nextInt(trackTiles.size());
                    Point next = (Point)trackTiles.remove(index);
                    Mob minecart = MobRegistry.getMob("minecart", (Level)this);
                    this.entityManager.addMob(minecart, next.x * 32 + 16, next.y * 32 + 16);
                    minecartsGenerated.add(minecart);
                }
                int tntCount = cg.random.getOneOfWeighted(Integer.class, 100, 0, 200, 1, 50, 2);
                for (int i = 0; i < tntCount && !tntArms.isEmpty(); ++i) {
                    int index = cg.random.nextInt(tntArms.size());
                    LinesGeneration next = (LinesGeneration)tntArms.remove(index);
                    int wireLength = cg.random.getIntBetween(10, 14) * cg.random.getOneOf(1, -1);
                    float lineLength = (float)new Point(next.x1, next.y1).distance(next.x2, next.y2);
                    Point2D.Float dir = GameMath.normalize(next.x1 - next.x2, next.y1 - next.y2);
                    float linePointLength = cg.random.getFloatBetween(0.0f, lineLength);
                    Point2D.Float linePoint = new Point2D.Float((float)next.x2 + dir.x * linePointLength, (float)next.y2 + dir.y * linePointLength);
                    Point2D.Float leverPoint = GameMath.getPerpendicularPoint(linePoint, 2.0f * Math.signum(wireLength), dir);
                    Point2D.Float tntPoint = GameMath.getPerpendicularPoint(linePoint, (float)wireLength, dir);
                    Line2D.Float wireLine = new Line2D.Float(leverPoint, tntPoint);
                    LinkedList tiles = new LinkedList();
                    LinesGeneration.pathTiles(wireLine, true, (fromTile, nextTile) -> tiles.add(nextTile));
                    for (Point tile : tiles) {
                        this.wireManager.setWire(tile.x, tile.y, 0, true);
                        if (!this.getObject((int)tile.x, (int)tile.y).isSolid) continue;
                        this.setObject(tile.x, tile.y, 0);
                    }
                    Point first = (Point)tiles.getFirst();
                    Point last = (Point)tiles.getLast();
                    this.setObject(first.x, first.y, ObjectRegistry.getObjectID("rocklever"));
                    this.setObject(last.x, last.y, ObjectRegistry.getObjectID("tnt"));
                }
            });
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("sandcaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("sandcaverocksmall"), 0.01f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.3f, 3, 6, ObjectRegistry.getObjectID("quartzsandstone"));
            cg.generateOreVeins(0.3f, 3, 6, ObjectRegistry.getObjectID("copperoresandstone"));
            cg.generateOreVeins(0.25f, 3, 6, ObjectRegistry.getObjectID("ironoresandstone"));
            cg.generateOreVeins(0.15f, 3, 6, ObjectRegistry.getObjectID("goldoresandstone"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        GameObject crystalClusterSmall = ObjectRegistry.getObject("amethystclustersmall");
        GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.005f, 4, 4.0f, 7.0f, 4.0f, 6.0f, lg -> {
            CellAutomaton ca = lg.doCellularAutomaton(cg.random);
            ca.streamAliveOrdered().forEachOrdered(tile -> {
                cg.addIllegalCrateTile(tile.x, tile.y);
                this.setTile(tile.x, tile.y, TileRegistry.getTileID("amethystgravel"));
                this.setObject(tile.x, tile.y, 0);
            });
            ca.streamAliveOrdered().forEachOrdered(tile -> {
                Point[] clearPoints;
                int rotation;
                if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.08f) && this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation = cg.random.nextInt(4), clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && this.getObjectID((int)tileX, (int)tileY) == 0)) {
                    ObjectRegistry.getObject(ObjectRegistry.getObjectID("amethystcluster")).placeObject(this, tile.x, tile.y, rotation, false);
                }
                if (cg.random.getChance(0.3f) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                    crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                }
            });
        });
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            this.preGeneratedStructures(cg, presets);
            for (int i = 0; i < 4; ++i) {
                AncientVultureArenaPreset arena = new AncientVultureArenaPreset(36, cg.random);
                presets.findRandomValidPositionAndApply(cg.random, 5, arena, 10, true, true);
            }
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.desertCaveChest, chestRoomRotation, ChestRoomSet.sandstone, ChestRoomSet.wood);
                chestRoom.replaceTile(TileRegistry.stoneFloorID, cg.random.getOneOf(TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID));
                chestRoom.replaceTile(TileRegistry.sandstoneFloorID, cg.random.getOneOf(TileRegistry.sandstoneFloorID, TileRegistry.sandstoneBrickFloorID));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }
            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);
            for (int i = 0; i < caveRuinsCount; ++i) {
                WallSet wallSet = cg.random.getOneOf(WallSet.sandstone, WallSet.wood);
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.palm, FurnitureSet.spruce);
                String floorStringID = cg.random.getOneOf("woodfloor", "woodfloor", "sandstonefloor", "sandstonebrickfloor");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.desertCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            int[] crates = new int[]{crate, vase};
            cg.generateRandomCrates(0.04f, crates);
            GenerationTools.spawnRandomPreset(this, new CavelingOasisPreset(cg.random), false, false, cg.random, true, 5, 1);
            this.postGeneratedStructures(cg, presets);
        });
        GenerationTools.checkValid(this);
        for (Mob mob : minecartsGenerated) {
            if (this.getObjectID(mob.getTileX(), mob.getTileY()) == trackObject) continue;
            mob.remove();
        }
    }

    protected void preGeneratedStructures(CaveGeneration cg, PresetGeneration presets) {
    }

    protected void postGeneratedStructures(CaveGeneration cg, PresetGeneration presets) {
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.desertCrate;
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "cave", "biome", this.getBiome(tileX, tileY).getLocalization());
    }

    @Override
    public float getLiquidSaltWaterSinkRate() {
        return 4.0f;
    }

    @Override
    public float getLiquidFreshWaterSinkRate() {
        return 10.0f;
    }

    @Override
    public float getLiquidMobSinkRate() {
        return 10.0f;
    }
}

