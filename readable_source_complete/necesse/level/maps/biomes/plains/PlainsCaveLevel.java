/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.plains;

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
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.plains.PlainsSurfaceLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.generationModules.RiverGeneration;
import necesse.level.maps.presets.ChieftainsArenaPreset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.RandomLootAreaPreset;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class PlainsCaveLevel
extends PlainsSurfaceLevel {
    public PlainsCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public PlainsCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "graniterocktile", "graniterock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel());
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("lavatile"), 1.0f, true);
        GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("watertile"), 1.0f, true);
        int riverCount = cg.random.getIntBetween(4, 6);
        for (int i = 0; i < riverCount; ++i) {
            RiverGeneration.generateOneSmallRiver(cg.random, this, "cavewatergrass", r -> r.getOneOf(RiverGeneration.WOOD_BRIDGE, RiverGeneration.STONE_BRIDGE));
        }
        this.liquidManager.calculateShores();
        PresetGeneration presets = new PresetGeneration(this);
        ChieftainsArenaPreset arenaPreset = new ChieftainsArenaPreset();
        int angle = cg.random.nextInt(360);
        Point2D.Float dir = GameMath.getAngleDir(angle);
        float offCenterDistance = cg.random.getFloatBetween(0.0f, 0.4f);
        Point centerPos = new Point(this.tileWidth / 2 + (int)(dir.x * ((float)(this.tileWidth / 2) * offCenterDistance)), this.tileHeight / 2 + (int)(dir.y * ((float)(this.tileHeight / 2) * offCenterDistance)));
        float centerRange = 15.5f;
        LinesGeneration lg = new LinesGeneration(centerPos.x, centerPos.y, centerRange);
        int armAngle = cg.random.nextInt(360);
        int arms = 3;
        int anglePerArm = 360 / arms;
        for (int i = 0; i < arms; ++i) {
            lg.addMultiArm(cg.random, armAngle += anglePerArm, 15, cg.random.getIntBetween(this.tileWidth / 2, (int)((float)this.tileWidth / 1.5f)), 5.0f, 10.0f, 7.0f, 8.0f, armLG -> !this.isTileWithinBounds(armLG.x2, armLG.y2, 10));
        }
        CellAutomaton ca = lg.doCellularAutomaton(cg.random);
        ca.cleanHardEdges();
        int x = centerPos.x - (int)Math.floor(centerRange);
        while ((double)x <= (double)centerPos.x + Math.ceil(centerRange)) {
            int y = centerPos.y - (int)Math.floor(centerRange);
            while ((double)y <= (double)centerPos.y + Math.ceil(centerRange)) {
                if (centerPos.distance(x, y) <= (double)centerRange) {
                    ca.setAlive(x, y);
                }
                ++y;
            }
            ++x;
        }
        int gravelID = TileRegistry.getTileID("graveltile");
        int graniteTileID = TileRegistry.getTileID("graniterocktile");
        ca.forEachTile(this, (level, tileX, tileY) -> {
            if (cg.random.getChance(0.25f)) {
                level.setTile(tileX, tileY, gravelID);
            } else {
                level.setTile(tileX, tileY, graniteTileID);
            }
            level.setObject(tileX, tileY, 0);
        });
        int x2 = centerPos.x - (int)Math.floor(centerRange);
        while ((double)x2 <= (double)centerPos.x + Math.ceil(centerRange)) {
            int y = centerPos.y - (int)Math.floor(centerRange);
            while ((double)y <= (double)centerPos.y + Math.ceil(centerRange)) {
                GameObject breakObject;
                if (centerPos.distance(x2, y) <= (double)centerRange && cg.random.getChance(0.05f) && (breakObject = ObjectRegistry.getObject(cg.random.getOneOf("crate", "vase"))).canPlace(this, x2, y, 0, false) == null) {
                    breakObject.placeObject(this, x2, y, 0, false);
                }
                ++y;
            }
            ++x2;
        }
        presets.addOccupiedSpace(centerPos.x - arenaPreset.width / 2, centerPos.y - arenaPreset.height / 2, arenaPreset.width, arenaPreset.height);
        int crate = ObjectRegistry.getObjectID("crate");
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
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("smallrunestone"), 5.0E-4f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("granitecaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("granitecaverocksmall"), 0.01f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.45f, 3, 6, ObjectRegistry.getObjectID("copperoregraniterock"));
            cg.generateOreVeins(0.35f, 3, 6, ObjectRegistry.getObjectID("ironoregraniterock"));
            cg.generateOreVeins(0.1f, 3, 6, ObjectRegistry.getObjectID("goldoregraniterock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            this.preGeneratedStructures(cg, presets);
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(16, 20);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.plainsCaveChest, chestRoomRotation, ChestRoomSet.dryad, ChestRoomSet.granite);
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
                WallSet wallSet = cg.random.getOneOf(WallSet.granite, WallSet.dryad);
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
                String floorStringID = cg.random.getOneOf("dryadfloor", "dryadfloor", "graniterock", "granitefloor");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.plainsCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            RuneboundTribePreset.generateRuneboundTribeOnLevel(this, cg.random, presets);
            arenaPreset.applyToLevel(this, centerPos.x - arenaPreset.width / 2, centerPos.y - arenaPreset.height / 2);
            this.postGeneratedStructures(cg, presets);
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
    }

    protected void preGeneratedStructures(CaveGeneration cg, PresetGeneration presets) {
    }

    protected void postGeneratedStructures(CaveGeneration cg, PresetGeneration presets) {
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.plainsCrate;
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

