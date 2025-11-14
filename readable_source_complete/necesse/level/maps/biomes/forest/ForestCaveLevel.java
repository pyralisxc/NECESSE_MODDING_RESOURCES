/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.forest;

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
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.generationModules.RiverGeneration;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.RandomLootAreaPreset;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class ForestCaveLevel
extends ForestSurfaceLevel {
    public ForestCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public ForestCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "rocktile", "rock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel());
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("lavatile"), 1.0f, true);
        GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("watertile"), 1.0f, true);
        int riverCount = cg.random.getIntBetween(4, 6);
        for (int i = 0; i < riverCount; ++i) {
            RiverGeneration.generateOneSmallRiver(cg.random, this, "cavewatergrass", r -> r.getOneOf(RiverGeneration.WOOD_BRIDGE, RiverGeneration.STONE_BRIDGE));
        }
        this.liquidManager.calculateShores();
        int crate = ObjectRegistry.getObjectID("crate");
        int trackObject = ObjectRegistry.getObjectID("minecarttrack");
        LinkedList minecartsGenerated = new LinkedList();
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.01f, 5, 7.0f, 15.0f, 3.0f, 5.0f, lg -> {
                LinesGeneration lgRoot = lg.getRoot();
                double centerDist = new Point(this.tileWidth / 2, this.tileHeight / 2).distance(lgRoot.x1, lgRoot.y1);
                if (centerDist >= 40.0) {
                    CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                    ca.forEachTile(this, (level, tileX, tileY) -> {
                        level.setTile(tileX, tileY, TileRegistry.spiderNestID);
                        if (cg.random.getChance(0.95f)) {
                            level.setObject(tileX, tileY, ObjectRegistry.cobWebID);
                        } else {
                            level.setObject(tileX, tileY, 0);
                        }
                    });
                    ca.spawnMobs(this, cg.random, "giantcavespider", 4, 8, 1, 4);
                }
            });
            GameObject crystalClusterSmall = ObjectRegistry.getObject("sapphireclustersmall");
            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.005f, 4, 4.0f, 7.0f, 4.0f, 6.0f, lg -> {
                CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                ca.streamAliveOrdered().forEachOrdered(tile -> {
                    cg.addIllegalCrateTile(tile.x, tile.y);
                    this.setTile(tile.x, tile.y, TileRegistry.getTileID("sapphiregravel"));
                    this.setObject(tile.x, tile.y, 0);
                });
                ca.streamAliveOrdered().forEachOrdered(tile -> {
                    Point[] clearPoints;
                    int rotation;
                    if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.08f) && this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation = cg.random.nextInt(4), clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && this.getObjectID((int)tileX, (int)tileY) == 0)) {
                        ObjectRegistry.getObject(ObjectRegistry.getObjectID("sapphirecluster")).placeObject(this, tile.x, tile.y, rotation, false);
                    }
                    if (cg.random.getChance(0.3f) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                        crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                    }
                });
            });
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
            AtomicInteger cryptRotation = new AtomicInteger();
            GameObject cryptGrass = ObjectRegistry.getObject("cryptgrass");
            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.015f, 2, 3.0f, 5.0f, 8.0f, 10.0f, lg -> {
                LinesGeneration lgRoot = lg.getRoot();
                double centerDist = new Point(this.tileWidth / 2, this.tileHeight / 2).distance(lgRoot.x1, lgRoot.y1);
                if (centerDist >= 40.0) {
                    CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                    ca.streamAliveOrdered().forEachOrdered(tile -> {
                        cg.addIllegalCrateTile(tile.x, tile.y);
                        this.setTile(tile.x, tile.y, TileRegistry.cryptAshID);
                        this.setObject(tile.x, tile.y, 0);
                    });
                    ca.placeEdgeWalls(this, WallSet.stone.wall, true);
                    ArrayList coffinTiles = new ArrayList();
                    ca.streamAliveOrdered().forEachOrdered(tile -> {
                        if (cg.random.getChance(0.2f) && cryptGrass.canPlace(this, tile.x, tile.y, 0, false) == null) {
                            cryptGrass.placeObject(this, tile.x, tile.y, 0, false);
                        }
                        if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0) {
                            if (cg.random.getChance(0.2f)) {
                                Point[] clearPoints;
                                int rotation = cg.random.nextInt(4);
                                if (this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1), new Point(0, -2)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && this.getObjectID((int)tileX, (int)tileY) == 0)) {
                                    ObjectRegistry.getObject(ObjectRegistry.getObjectID("stonecoffin")).placeObject(this, tile.x, tile.y, rotation, false);
                                    coffinTiles.add(tile);
                                }
                            } else if (cg.random.getChance(0.06f)) {
                                this.setObject(tile.x, tile.y, ObjectRegistry.getObjectID("stonecolumn"));
                            } else if (cg.random.getChance(0.3f)) {
                                cg.random.runOneOf(() -> this.setObject(tile.x, tile.y, ObjectRegistry.getObjectID("gravestone1"), cg.random.nextInt(4)), () -> this.setObject(tile.x, tile.y, ObjectRegistry.getObjectID("gravestone2"), cg.random.nextInt(4)));
                            }
                        }
                    });
                    if (!coffinTiles.isEmpty()) {
                        Point tile2 = (Point)cg.random.getOneOf(coffinTiles);
                        Point tile22 = (Point)cg.random.getOneOf(coffinTiles);
                        LootTablePresets.caveCryptUniqueItems.applyToLevel(cg.random, this.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this, tile2.x, tile2.y, this, cryptRotation);
                    }
                    for (Point tile3 : coffinTiles) {
                        LootTablePresets.caveCryptCoffin.applyToLevel(cg.random, this.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this, tile3.x, tile3.y, this, cryptRotation);
                    }
                    ca.spawnMobs(this, cg.random, "vampire", 25, 45, 1, 4);
                }
            });
            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.002f, 4, 4.0f, 7.0f, 4.0f, 6.0f, lg -> {
                CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                ArrayList validChestTiles = new ArrayList();
                ca.streamAliveOrdered().forEachOrdered(tile -> {
                    cg.addIllegalCrateTile(tile.x, tile.y);
                    this.setTile(tile.x, tile.y, TileRegistry.mudID);
                    this.setObject(tile.x, tile.y, 0);
                    validChestTiles.add(new Point((Point)tile));
                    GameTile gravelTile = TileRegistry.getTile(TileRegistry.gravelID);
                    if (cg.random.getChance(0.3f) && gravelTile.canPlace(this, tile.x, tile.y, false) == null) {
                        gravelTile.placeTile(this, tile.x, tile.y, false);
                    }
                });
                Point chestTile = (Point)cg.random.getOneOf(validChestTiles);
                if (chestTile != null) {
                    this.setObject(chestTile.x, chestTile.y, ObjectRegistry.getObjectID("barrel"));
                    LootTablePresets.bearBarrel.applyToLevel(cg.random, 1.0f, this, chestTile.x, chestTile.y, new Object[0]);
                    Mob grizzlyBear = MobRegistry.getMob("grizzlybear", (Level)this);
                    grizzlyBear.canDespawn = false;
                    Point spawnPos = PortalObjectEntity.getTeleportDestinationAroundObject(this, grizzlyBear, chestTile.x, chestTile.y, true);
                    if (spawnPos == null) {
                        spawnPos = new Point(chestTile.x * 32 + 16, chestTile.y * 32 + 16);
                    }
                    grizzlyBear.onSpawned(spawnPos.x, spawnPos.y);
                    this.entityManager.addMob(grizzlyBear, spawnPos.x, spawnPos.y);
                }
            });
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverocksmall"), 0.01f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("beetcavecroppler"), 5.0E-4f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.25f, 5, 10, ObjectRegistry.getObjectID("clayrock"));
            cg.generateOreVeins(0.45f, 3, 6, ObjectRegistry.getObjectID("copperorerock"));
            cg.generateOreVeins(0.35f, 3, 6, ObjectRegistry.getObjectID("ironorerock"));
            cg.generateOreVeins(0.1f, 3, 6, ObjectRegistry.getObjectID("goldorerock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            this.preGeneratedStructures(cg, presets);
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(16, 20);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.basicCaveChest, chestRoomRotation, ChestRoomSet.stone, ChestRoomSet.wood);
                chestRoom.replaceTile(TileRegistry.stoneFloorID, cg.random.getOneOf(TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }
            int lootAreaAmount = cg.random.getIntBetween(5, 10);
            for (int i = 0; i < lootAreaAmount; ++i) {
                TicketSystemList<String> mobs = new TicketSystemList<String>();
                mobs.addObject(100000, (Object)"trenchcoatgoblinstacked", true);
                mobs.addObject(100, (Object)"goblin");
                RandomLootAreaPreset lootArea = new RandomLootAreaPreset(cg.random, 15, "stonecolumn", mobs);
                presets.findRandomValidPositionAndApply(cg.random, 5, lootArea, 10, true, true);
            }
            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);
            for (int i = 0; i < caveRuinsCount; ++i) {
                WallSet wallSet = cg.random.getOneOf(WallSet.stone, WallSet.wood);
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
                String floorStringID = cg.random.getOneOf("woodfloor", "woodfloor", "stonefloor", "stonebrickfloor");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.basicCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            cg.generateRandomCrates(0.03f, crate);
            this.postGeneratedStructures(cg, presets);
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
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

