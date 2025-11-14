/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.snow;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.snow.SnowCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.MusicPlayerPreset2;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class SnowDeepCaveLevel
extends SnowCaveLevel {
    public SnowDeepCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SnowDeepCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    @Override
    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "deepsnowrocktile", "deepsnowrock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.44f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GameTile deepIceTile = TileRegistry.getTile("deepicetile");
            GenerationTools.generateRandomSmoothVeins(this, cg.random, 0.06f, 2, 7.0f, 20.0f, 3.0f, 8.0f, (level, tileX, tileY) -> {
                deepIceTile.placeTile(level, tileX, tileY, false);
                level.setObject(tileX, tileY, 0);
            });
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsnowcaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsnowcaverocksmall"), 0.01f);
            int icicleTrigger = ObjectRegistry.getObjectID("fallingicicletrigger");
            GenerationTools.generateRandomPoints(this, cg.random, 0.5f, veinPos -> {
                if (this.getObjectID(veinPos.x, veinPos.y) == 0) {
                    GenerationTools.placeRandomVein(this, cg.random, veinPos.x, veinPos.y, 7, 20, -1, -1, 0.0f, icicleTrigger, -1, 0.4f, false, false);
                }
            });
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("copperoredeepsnowrock"));
            cg.generateOreVeins(0.25f, 3, 6, ObjectRegistry.getObjectID("ironoredeepsnowrock"));
            cg.generateOreVeins(0.15f, 3, 6, ObjectRegistry.getObjectID("goldoredeepsnowrock"));
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("tungstenoredeepsnowrock"));
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("lifequartzdeepsnowrock"));
            cg.generateOreVeins(0.17f, 3, 6, ObjectRegistry.getObjectID("glacialoredeepsnowrock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            this.preGeneratedStructures(cg, presets);
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.deepSnowCaveChest, chestRoomRotation, ChestRoomSet.deepStone, ChestRoomSet.deepSnowStone);
                chestRoom.replaceTile(TileRegistry.deepStoneFloorID, cg.random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
                chestRoom.replaceTile(TileRegistry.deepSnowStoneFloorID, cg.random.getOneOf(TileRegistry.deepSnowStoneFloorID, TileRegistry.deepSnowStoneBrickFloorID));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }
            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);
            for (int i = 0; i < caveRuinsCount; ++i) {
                WallSet wallSet = cg.random.getOneOf(WallSet.deepStone, WallSet.deepSnowStone);
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.pine, FurnitureSet.spruce);
                String floorStringID = cg.random.getOneOf("deepsnowstonefloor", "deepsnowstonebrickfloor");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.snowDeepCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            presets.findRandomValidPositionAndApply(cg.random, 50, new MusicPlayerPreset2(cg.random), 10, true, false);
            cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("snowcrate"));
            this.postGeneratedStructures(cg, presets);
        });
        GenerationTools.checkValid(this);
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.snowDeepCrate;
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "deepcave", "biome", this.getBiome(tileX, tileY).getLocalization());
    }
}

