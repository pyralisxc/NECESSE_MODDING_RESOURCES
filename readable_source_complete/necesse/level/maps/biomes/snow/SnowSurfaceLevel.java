/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.snow;

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
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.ChristmasHousePreset;
import necesse.level.maps.presets.FarmersRefugePreset;
import necesse.level.maps.presets.FishingHutPreset;
import necesse.level.maps.presets.HunterCabinPreset;
import necesse.level.maps.presets.MerchantsAmbushPreset;
import necesse.level.maps.presets.RandomRuinsPreset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class SnowSurfaceLevel
extends Level {
    public SnowSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SnowSurfaceLevel(int islandX, int islandY, float islandSize, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 100.0f) + 20;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.waterID;
        int snowTile = TileRegistry.snowID;
        int iceTile = TileRegistry.iceID;
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), e -> {
            if (ig.random.getChance(0.05f)) {
                ig.generateSimpleIsland(this.tileWidth / 2, this.tileHeight / 2, waterTile, snowTile, iceTile);
            } else {
                ig.generateShapedIsland(waterTile, snowTile, iceTile);
            }
            int rivers = ig.random.getIntBetween(1, 5);
            for (int i = 0; !(i >= rivers || i > 0 && ig.random.getChance(0.4f)); ++i) {
                ig.generateRiver(waterTile, snowTile, iceTile);
            }
            ig.generateLakes(0.02f, waterTile, snowTile, iceTile);
            ig.clearTinyIslands(waterTile);
            this.liquidManager.calculateFull();
        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), e -> {
            int treeObject = ObjectRegistry.getObjectID("pinetree");
            ig.generateCellMapObjects(0.4f, treeObject, snowTile, 0.08f);
            ig.generateObjects(ObjectRegistry.getObjectID("snowpile0"), snowTile, 0.05f);
            ig.generateObjects(ObjectRegistry.getObjectID("snowpile1"), snowTile, 0.05f);
            ig.generateObjects(ObjectRegistry.getObjectID("snowpile2"), snowTile, 0.05f);
            ig.generateObjects(ObjectRegistry.getObjectID("snowpile3"), snowTile, 0.05f);
            ig.generateObjects(ObjectRegistry.getObjectID("blueflowerpatch"), snowTile, 0.005f);
            ig.generateObjects(ObjectRegistry.getObjectID("snowsurfacerock"), -1, 0.001f, false);
            ig.generateObjects(ObjectRegistry.getObjectID("snowsurfacerocksmall"), -1, 0.002f, false);
            ig.generateFruitGrowerVeins("blackberrybush", 0.04f, 8, 10, 0.1f, null, snowTile);
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03f, 5, 15, snowTile, ObjectRegistry.getObjectID("blueflowerpatch"), 0.4f, false);
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03f, 6, 12, snowTile, ObjectRegistry.getObjectID("wildiceblossom"), 0.2f, false);
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), e -> {
            PresetGeneration presets = new PresetGeneration(this);
            this.preGeneratedStructures(islandSize, ig, presets);
            presets.findRandomValidPositionAndApply(ig.random, 40, new RandomRuinsPreset(ig.random).setTiles("woodfloor", "snowstonefloor").setWalls("woodwall", "snowstonewall"), 10, false, false);
            float chanceToAddMiniBiome = 0.75f;
            TicketSystemList miniBiomesTicketList = new TicketSystemList();
            if (!this.baseBiome.hasVillage()) {
                miniBiomesTicketList.addObject(85, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.maple, FurnitureSet.pine, FurnitureSet.birch);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new HunterCabinPreset(ig.random, furnitureSet, wallSet), 10, true, true);
                });
                miniBiomesTicketList.addObject(80, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.dungeon, FurnitureSet.pine, FurnitureSet.oak, FurnitureSet.maple);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FarmersRefugePreset(ig.random, furnitureSet, wallSet, TreeSet.pine, BushSet.blackberry), 10, true, false, false);
                });
                miniBiomesTicketList.addObject(65, () -> {
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone);
                    int tileID = ig.random.getOneOf(TileRegistry.snowStonePathID, TileRegistry.woodPathID, TileRegistry.stonePathID);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FishingHutPreset(ig.random, wallSet, tileID), 10, true, true);
                });
                miniBiomesTicketList.addObject(60, () -> presets.findRandomValidPositionAndApply(ig.random, 200, new MerchantsAmbushPreset(ig.random, false), 10, true, true));
                miniBiomesTicketList.addObject(40, () -> presets.findRandomValidPositionAndApply(ig.random, 200, new ChristmasHousePreset(ig.random), 10, false, false));
            }
            while (!miniBiomesTicketList.isEmpty() && ig.random.getChance(chanceToAddMiniBiome)) {
                Runnable miniBiome = (Runnable)miniBiomesTicketList.getAndRemoveRandomObject(ig.random);
                miniBiome.run();
                chanceToAddMiniBiome -= 0.15f;
            }
            this.postGeneratedStructures(islandSize, ig, presets);
        });
        GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, islandSize, ig), e -> {
            ProtectedTicketSystemList sheepSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "sheep")).addObject(25, "ram");
            ig.spawnMobHerds((TicketSystemList<String>)sheepSpawns, ig.random.getIntBetween(20, 40), snowTile, 2, 6, islandSize);
            ig.spawnMobHerds("penguin", ig.random.getIntBetween(20, 40), snowTile, 2, 6, islandSize);
            ig.spawnMobHerds("polarbear", ig.random.getIntBetween(5, 10), snowTile, 1, 1, islandSize);
        });
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

