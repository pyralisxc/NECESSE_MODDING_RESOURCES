/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.forest;

import java.awt.Point;
import java.awt.Rectangle;
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
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class ForestDeepCaveLevel
extends ForestCaveLevel {
    public ForestDeepCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public ForestDeepCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    @Override
    public void generateLevel() {
        int deepRockTile = TileRegistry.getTileID("deeprocktile");
        CaveGeneration cg = new CaveGeneration(this, "deeprocktile", "deeprock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.44f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomObjectVeinsOnTile(this, cg.random, 0.2f, 4, 8, deepRockTile, ObjectRegistry.getObjectID("wildcaveglow"), 0.2f, false);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07f, 2, 7.0f, 20.0f, 3.0f, 8.0f, TileRegistry.getTileID("lavatile"), 1.0f, true);
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverocksmall"), 0.01f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("copperoredeeprock"));
            cg.generateOreVeins(0.25f, 3, 6, ObjectRegistry.getObjectID("ironoredeeprock"));
            cg.generateOreVeins(0.15f, 3, 6, ObjectRegistry.getObjectID("goldoredeeprock"));
            cg.generateOreVeins(0.25f, 5, 10, ObjectRegistry.getObjectID("obsidianrock"));
            cg.generateOreVeins(0.2f, 3, 6, ObjectRegistry.getObjectID("tungstenoredeeprock"));
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("lifequartzdeeprock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        GameObject crystalClusterSmall = ObjectRegistry.getObject("rubyclustersmall");
        GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.005f, 4, 4.0f, 7.0f, 4.0f, 6.0f, lg -> {
            CellAutomaton ca = lg.doCellularAutomaton(cg.random);
            ca.streamAliveOrdered().forEachOrdered(tile -> {
                cg.addIllegalCrateTile(tile.x, tile.y);
                this.setTile(tile.x, tile.y, TileRegistry.getTileID("rubygravel"));
                this.setObject(tile.x, tile.y, 0);
            });
            ca.streamAliveOrdered().forEachOrdered(tile -> {
                Point[] clearPoints;
                int rotation;
                if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.08f) && this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation = cg.random.nextInt(4), clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && this.getObjectID((int)tileX, (int)tileY) == 0)) {
                    ObjectRegistry.getObject(ObjectRegistry.getObjectID("rubycluster")).placeObject(this, tile.x, tile.y, rotation, false);
                }
                if (cg.random.getChance(0.3f) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                    crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                }
            });
        });
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            this.preGeneratedStructures(cg, presets);
            int abandonedMineCount = cg.random.getIntBetween(2, 3);
            for (int i = 0; i < abandonedMineCount; ++i) {
                Rectangle abandonedMineRec = AbandonedMinePreset.generateAbandonedMineOnLevel(this, cg.random, presets.getOccupiedSpace());
                if (abandonedMineRec == null) continue;
                presets.addOccupiedSpace(abandonedMineRec);
            }
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.deepCaveChest, chestRoomRotation, ChestRoomSet.deepStone, ChestRoomSet.obsidian);
                chestRoom.replaceTile(TileRegistry.deepStoneFloorID, cg.random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }
            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);
            for (int i = 0; i < caveRuinsCount; ++i) {
                WallSet wallSet = cg.random.getOneOf(WallSet.deepStone, WallSet.obsidian);
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
                String floorStringID = cg.random.getOneOf("deepstonefloor", "deepstonebrickfloor");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.basicDeepCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("crate"));
            this.postGeneratedStructures(cg, presets);
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.basicDeepCrate;
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "deepcave", "biome", this.getBiome(tileX, tileY).getLocalization());
    }
}

