/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.swamp;

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
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.BrokenHusbandryFencePreset;
import necesse.level.maps.presets.FarmersRefugePreset;
import necesse.level.maps.presets.FishingHutPreset;
import necesse.level.maps.presets.HunterCabinPreset;
import necesse.level.maps.presets.MerchantsAmbushPreset;
import necesse.level.maps.presets.RandomRuinsPreset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.presets.worldStructures.WitchCabinMadsPreset;

public class SwampSurfaceLevel
extends Level {
    public SwampSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SwampSurfaceLevel(int islandX, int islandY, float islandSize, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 100.0f) + 20;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.waterID;
        int grassTile = TileRegistry.swampGrassID;
        int overgrownGrassTile = TileRegistry.overgrownSwampGrassID;
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), e -> {
            for (int i = 0; i < 25; ++i) {
                ig.generateRandomCellIsland(ig.random.getIntBetween(10, 40), ig.random.getIntBetween(50, this.tileWidth - 50), ig.random.getIntBetween(50, this.tileHeight - 50));
            }
            ig.cellMap = GenerationTools.doCellularAutomaton(ig.cellMap, this.tileWidth, this.tileHeight, 5, 4, false, 4);
            ig.updateCellMap(grassTile, waterTile);
            GenerationTools.smoothTile(this, grassTile);
            this.liquidManager.calculateFull();
        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), e -> {
            GenerationTools.generateRandomVeins(this, ig.random, 0.15f, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.7f) && this.getTileID(tileX, tileY) == grassTile) {
                    level.setTile(tileX, tileY, overgrownGrassTile);
                }
            });
            GameTile mudTile = TileRegistry.getTile(TileRegistry.mudID);
            GameObject wildMushroom = ObjectRegistry.getObject(ObjectRegistry.getObjectID("wildmushroom"));
            GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.1f, 4, 4.0f, 7.0f, 3.0f, 5.0f, (level, tileX, tileY) -> {
                if (level.getTileID(tileX, tileY) == grassTile) {
                    if (ig.random.getChance(0.7f)) {
                        mudTile.placeTile(level, tileX, tileY, false);
                    }
                    if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.1f) && wildMushroom.canPlace(level, tileX, tileY, 0, false) == null) {
                        wildMushroom.placeObject(level, tileX, tileY, 0, false);
                    }
                }
            });
            int willowTree = ObjectRegistry.getObjectID("willowtree");
            int grassObject = ObjectRegistry.getObjectID("swampgrass");
            ig.generateCellMapObjects(0.35f, willowTree, grassTile, 0.08f);
            ig.generateObjects(grassObject, grassTile, 0.5f);
            ig.generateObjects(grassObject, overgrownGrassTile, 0.5f);
            ig.generateObjects(ObjectRegistry.getObjectID("purpleflowerpatch"), grassTile, 0.005f);
            ig.generateObjects(ObjectRegistry.getObjectID("willowtreestump"), grassTile, 0.001f);
            ig.generateObjects(ObjectRegistry.getObjectID("swampsurfacerock"), -1, 0.001f, false);
            ig.generateObjects(ObjectRegistry.getObjectID("swampsurfacerocksmall"), -1, 0.002f, false);
            ig.generateObjects(ObjectRegistry.getObjectID("swamproot"), grassTile, 0.008f);
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03f, 5, 15, grassTile, ObjectRegistry.getObjectID("purpleflowerpatch"), 0.4f, false);
            GameObject cattail = ObjectRegistry.getObject("cattail");
            GameObject reeds = ObjectRegistry.getObject("reeds");
            GenerationTools.generateRandomVeins(this, ig.random, 0.2f, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.2f) && reeds.canPlace(level, tileX, tileY, 0, false) == null) {
                    reeds.placeObject(level, tileX, tileY, 0, false);
                }
                if (ig.random.getChance(0.2f) && cattail.canPlace(level, tileX, tileY, 0, false) == null) {
                    cattail.placeObject(level, tileX, tileY, 0, false);
                }
            });
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), e -> {
            PresetGeneration presets = new PresetGeneration(this);
            this.preGeneratedStructures(islandSize, ig, presets);
            presets.findRandomValidPositionAndApply(ig.random, 40, new RandomRuinsPreset(ig.random).setTiles("swampstonefloor").setWalls("swampstonewall"), 10, false, false);
            presets.findRandomValidPositionAndApply(ig.random, 200, new WitchCabinMadsPreset(ig.random), 10, true, true);
            float chanceToAddMiniBiome = 0.75f;
            TicketSystemList miniBiomesTicketList = new TicketSystemList();
            if (!this.baseBiome.hasVillage()) {
                miniBiomesTicketList.addObject(85, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.swampStone);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new HunterCabinPreset(ig.random, furnitureSet, wallSet), 10, true, true);
                });
                miniBiomesTicketList.addObject(70, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.swampStone);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FarmersRefugePreset(ig.random, furnitureSet, wallSet, TreeSet.willow, BushSet.blackberry), 10, true, false, false);
                });
                miniBiomesTicketList.addObject(55, () -> {
                    FenceSet fenceSet = ig.random.getOneOf(FenceSet.wood, FenceSet.stone, FenceSet.iron);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new BrokenHusbandryFencePreset(ig.random, fenceSet), 10, true, true);
                });
                miniBiomesTicketList.addObject(55, () -> {
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.swampStone);
                    int tileID = ig.random.getOneOf(TileRegistry.woodPathID, TileRegistry.stonePathID);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FishingHutPreset(ig.random, wallSet, tileID), 10, true, true);
                });
                miniBiomesTicketList.addObject(60, () -> presets.findRandomValidPositionAndApply(ig.random, 200, new MerchantsAmbushPreset(ig.random, true), 10, true, true));
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
            ig.spawnMobHerds((TicketSystemList<String>)sheepSpawns, ig.random.getIntBetween(15, 30), grassTile, 2, 6, islandSize);
            ProtectedTicketSystemList cowSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "cow")).addObject(25, "bull");
            ig.spawnMobHerds((TicketSystemList<String>)cowSpawns, ig.random.getIntBetween(10, 25), grassTile, 2, 6, islandSize);
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

