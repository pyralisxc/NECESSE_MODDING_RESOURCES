/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.desert;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateIslandAnimalsEvent;
import necesse.engine.events.worldGeneration.GenerateIslandFloraEvent;
import necesse.engine.events.worldGeneration.GenerateIslandLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateIslandStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandAnimalsEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandFloraEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.FarmersRefugePreset;
import necesse.level.maps.presets.FishingHutPreset;
import necesse.level.maps.presets.HunterCabinPreset;
import necesse.level.maps.presets.MerchantsAmbushPreset;
import necesse.level.maps.presets.RandomRuinsPreset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class DesertSurfaceLevel
extends Level {
    public DesertSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DesertSurfaceLevel(int islandX, int islandY, float islandSize, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 90.0f) + 40;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.getTileID("watertile");
        int sandTile = TileRegistry.getTileID("sandtile");
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), e -> {
            if (ig.random.getChance(0.05f)) {
                ig.generateSimpleIsland(this.tileWidth / 2, this.tileHeight / 2, waterTile, sandTile, -1);
            } else {
                ig.generateShapedIsland(waterTile, sandTile, -1);
            }
            int rivers = ig.random.getIntBetween(1, 3);
            for (int i = 0; !(i >= rivers || i > 0 && ig.random.getChance(0.4f)); ++i) {
                ig.generateRiver(waterTile, sandTile, -1);
            }
            ig.generateLakes(0.01f, waterTile, sandTile, -1);
            ig.clearTinyIslands(waterTile);
            this.liquidManager.calculateFull();
        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), e -> {
            ig.generateObjects(ObjectRegistry.getObjectID("sandsurfacerock"), -1, 0.001f, false);
            ig.generateObjects(ObjectRegistry.getObjectID("sandsurfacerocksmall"), -1, 0.002f, false);
            int cactusObject = ObjectRegistry.getObjectID("cactus");
            ig.generateObjects(cactusObject, sandTile, 0.01f);
            int palmTreeObject = ObjectRegistry.getObjectID("palmtree");
            ig.generateObjects(palmTreeObject, sandTile, 0.002f);
            ig.generateFruitGrowerSingle("coconuttree", 0.03f, sandTile);
            ig.generateObjects(ObjectRegistry.getObjectID("cowskeleton"), sandTile, 4.0E-4f);
            GameObject waterPlant = ObjectRegistry.getObject(ObjectRegistry.getObjectID("watergrass"));
            GenerationTools.generateRandomVeins(this, ig.random, 0.15f, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.3f) && waterPlant.canPlace(level, tileX, tileY, 0, false) == null && level.liquidManager.isFreshWater(tileX, tileY)) {
                    waterPlant.placeObject(level, tileX, tileY, 0, false);
                }
            });
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), e -> {
            PresetGeneration presets = new PresetGeneration(this);
            this.preGeneratedStructures(islandSize, ig, presets);
            presets.findRandomValidPositionAndApply(ig.random, 40, new RandomRuinsPreset(ig.random).setTiles("woodfloor", "sandstonefloor").setWalls("woodwall", "sandstonewall"), 10, false, false);
            float chanceToAddMiniBiome = 0.65f;
            TicketSystemList miniBiomesTicketList = new TicketSystemList();
            if (!this.baseBiome.hasVillage()) {
                miniBiomesTicketList.addObject(90, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.palm, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.palm, WallSet.sandstone);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new HunterCabinPreset(ig.random, furnitureSet, wallSet), 10, true, true);
                });
                miniBiomesTicketList.addObject(80, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.palm, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.palm, WallSet.sandstone);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FarmersRefugePreset(ig.random, furnitureSet, wallSet, TreeSet.palm, BushSet.raspberry), 10, true, false, false);
                });
                miniBiomesTicketList.addObject(65, () -> {
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.palm, WallSet.sandstone);
                    int tileID = ig.random.getOneOf(TileRegistry.woodPathID, TileRegistry.stonePathID, TileRegistry.sandstonePathID);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FishingHutPreset(ig.random, wallSet, tileID), 10, true, true);
                });
                miniBiomesTicketList.addObject(60, () -> presets.findRandomValidPositionAndApply(ig.random, 200, new MerchantsAmbushPreset(ig.random, false), 10, true, true));
            }
            while (!miniBiomesTicketList.isEmpty() && ig.random.getChance(chanceToAddMiniBiome)) {
                Runnable miniBiome = (Runnable)miniBiomesTicketList.getAndRemoveRandomObject(ig.random);
                miniBiome.run();
                chanceToAddMiniBiome -= 0.15f;
            }
            this.postGeneratedStructures(islandSize, ig, presets);
        });
        GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, islandSize, ig), e -> GenerationTools.spawnMobHerds((Level)this, ig.random, "wildostrich", 1, sandTile, 1, 1));
        GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, islandSize, ig));
        GenerationTools.checkValid(this);
    }

    protected void preGeneratedStructures(float islandSize, IslandGeneration ig, PresetGeneration presets) {
    }

    protected void postGeneratedStructures(float islandSize, IslandGeneration ig, PresetGeneration presets) {
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "surface", "biome", this.getBiome(tileX, tileY).getLocalization());
    }
}

