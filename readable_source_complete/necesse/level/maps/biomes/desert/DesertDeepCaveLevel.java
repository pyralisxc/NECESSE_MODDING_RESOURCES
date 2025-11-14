/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.desert;

import java.awt.Point;
import java.awt.geom.Point2D;
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
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.desert.DesertCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.furniturePresets.BedDresserPreset;
import necesse.level.maps.presets.furniturePresets.BenchPreset;
import necesse.level.maps.presets.furniturePresets.BookshelfClockPreset;
import necesse.level.maps.presets.furniturePresets.BookshelvesPreset;
import necesse.level.maps.presets.furniturePresets.CabinetsPreset;
import necesse.level.maps.presets.furniturePresets.DeskBookshelfPreset;
import necesse.level.maps.presets.furniturePresets.DinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.DisplayStandClockPreset;
import necesse.level.maps.presets.furniturePresets.ModularDinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.ModularTablesPreset;
import necesse.level.maps.presets.furniturePresets.SingleChestPreset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class DesertDeepCaveLevel
extends DesertCaveLevel {
    public DesertDeepCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DesertDeepCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    @Override
    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "deepsandstonetile", "deepsandstonerock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.44f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.05f, 2, 7.0f, 20.0f, 3.0f, 8.0f, TileRegistry.getTileID("lavatile"), 1.0f, true);
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsandcaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsandcaverocksmall"), 0.01f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("copperoredeepsandstonerock"));
            cg.generateOreVeins(0.25f, 3, 6, ObjectRegistry.getObjectID("ironoredeepsandstonerock"));
            cg.generateOreVeins(0.15f, 3, 6, ObjectRegistry.getObjectID("goldoredeepsandstonerock"));
            cg.generateOreVeins(0.17f, 3, 6, ObjectRegistry.getObjectID("ancientfossiloredeepsnowrock"));
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("lifequartzdeepsandstonerock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            this.preGeneratedStructures(cg, presets);
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.deepDesertCaveChest, chestRoomRotation, ChestRoomSet.deepSandstone, ChestRoomSet.obsidian);
                chestRoom.replaceTile(TileRegistry.deepStoneFloorID, cg.random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }
            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);
            for (int i = 0; i < caveRuinsCount; ++i) {
                WallSet wallSet = cg.random.getOneOf(WallSet.deepSandstone, WallSet.obsidian);
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.palm, FurnitureSet.spruce);
                String floorStringID = cg.random.getOneOf("deepstonefloor", "deepstonebrickfloor");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.desertDeepCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            int angle = cg.random.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float offCenterDistance = cg.random.getFloatBetween(0.0f, 0.4f);
            Point pos = new Point(this.tileWidth / 2 + (int)(dir.x * ((float)this.tileWidth / 2.0f * offCenterDistance)), this.tileHeight / 2 + (int)(dir.y * ((float)this.tileHeight / 2.0f * offCenterDistance)));
            float centerRange = 15.5f;
            LinesGeneration lg = new LinesGeneration(pos.x, pos.y, centerRange);
            int armAngle = cg.random.nextInt(360);
            int arms = 8;
            int anglePerArm = 360 / arms;
            for (int i = 0; i < arms; ++i) {
                lg.addMultiArm(cg.random, armAngle += anglePerArm, 15, cg.random.getIntBetween(this.tileWidth / 2, (int)((float)this.tileWidth / 1.5f)), 5.0f, 10.0f, 7.0f, 8.0f, armLG -> armLG.x2 < 10 || armLG.x2 > this.tileWidth - 10 || armLG.y2 < 10 || armLG.y2 > this.tileHeight - 10);
            }
            CellAutomaton ca = lg.doCellularAutomaton(cg.random);
            ca.cleanHardEdges();
            int x = pos.x - (int)Math.floor(centerRange);
            while ((double)x <= (double)pos.x + Math.ceil(centerRange)) {
                int y = pos.y - (int)Math.floor(centerRange);
                while ((double)y <= (double)pos.y + Math.ceil(centerRange)) {
                    if (pos.distance(x, y) <= (double)centerRange) {
                        ca.setAlive(x, y);
                    }
                    ++y;
                }
                ++x;
            }
            int sandBrickID = TileRegistry.getTileID("sandbrick");
            int woodFloorID = TileRegistry.getTileID("woodfloor");
            ca.forEachTile(this, (level, tileX, tileY) -> {
                if (cg.random.getChance(0.75f)) {
                    level.setTile(tileX, tileY, sandBrickID);
                } else {
                    level.setTile(tileX, tileY, woodFloorID);
                }
                level.setObject(tileX, tileY, 0);
            });
            ca.placeEdgeWalls(this, ObjectRegistry.getObjectID("deepsandstonewall"), true);
            ca.forEachTile(this, (level, tileX, tileY) -> {
                GameObject breakObject;
                if (cg.random.getChance(0.02f) && (breakObject = ObjectRegistry.getObject(cg.random.getOneOf("crate", "vase"))).canPlace(level, tileX, tileY, 0, false) == null) {
                    breakObject.placeObject(level, tileX, tileY, 0, false);
                }
            });
            int x2 = pos.x - (int)Math.floor(centerRange);
            while ((double)x2 <= (double)pos.x + Math.ceil(centerRange)) {
                int y = pos.y - (int)Math.floor(centerRange);
                while ((double)y <= (double)pos.y + Math.ceil(centerRange)) {
                    GameObject breakObject;
                    if (pos.distance(x2, y) <= (double)centerRange && cg.random.getChance(0.05f) && (breakObject = ObjectRegistry.getObject(cg.random.getOneOf("crate", "vase"))).canPlace(this, x2, y, 0, false) == null) {
                        breakObject.placeObject(this, x2, y, 0, false);
                    }
                    ++y;
                }
                ++x2;
            }
            LootTable templeChestLootTable = new LootTable();
            for (int i = 0; i < 5; ++i) {
                templeChestLootTable.items.add(this.getCrateLootTable());
            }
            TicketSystemList<Preset> templeFurniture = new TicketSystemList<Preset>();
            templeFurniture.addObject(100, (Object)new BedDresserPreset(FurnitureSet.palm, 2));
            templeFurniture.addObject(100, (Object)new BenchPreset(FurnitureSet.palm, 2));
            templeFurniture.addObject(100, (Object)new BookshelfClockPreset(FurnitureSet.palm, 2));
            templeFurniture.addObject(100, (Object)new BookshelvesPreset(FurnitureSet.palm, 2, 3));
            templeFurniture.addObject(100, (Object)new CabinetsPreset(FurnitureSet.palm, 2, 3));
            templeFurniture.addObject(100, (Object)new DeskBookshelfPreset(FurnitureSet.palm, 2));
            templeFurniture.addObject(100, (Object)new DinnerTablePreset(FurnitureSet.palm, 2));
            templeFurniture.addObject(100, (Object)new DisplayStandClockPreset(FurnitureSet.palm, 2, cg.random, null, new Object[0]));
            templeFurniture.addObject(100, (Object)new ModularDinnerTablePreset(FurnitureSet.palm, 2, 1));
            templeFurniture.addObject(100, (Object)new ModularTablesPreset(FurnitureSet.palm, 2, 2, true));
            templeFurniture.addObject(100, (Object)new SingleChestPreset(FurnitureSet.palm, 2, cg.random, templeChestLootTable, new Object[0]));
            ca.placeFurniturePresets(templeFurniture, 0.4f, this, cg.random);
            int columnsCount = cg.random.getIntBetween(6, 8);
            int anglePerColumn = 360 / columnsCount;
            int columnAngle = cg.random.nextInt(360);
            float columnDistance = centerRange - centerRange / 3.0f;
            int columnID = ObjectRegistry.getObjectID("deepsandstonecolumn");
            for (int i = 0; i < columnsCount; ++i) {
                Point2D.Float columnDir = GameMath.getAngleDir(columnAngle += cg.random.getIntOffset(anglePerColumn, anglePerColumn / 5));
                this.setObject(pos.x + (int)(columnDir.x * columnDistance), pos.y + (int)(columnDir.y * columnDistance), columnID);
            }
            this.setObject(pos.x, pos.y, ObjectRegistry.getObjectID("templepedestal"));
            cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("vase"));
            this.postGeneratedStructures(cg, presets);
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.desertDeepCrate;
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "deepcave", "biome", this.getBiome(tileX, tileY).getLocalization());
    }
}

