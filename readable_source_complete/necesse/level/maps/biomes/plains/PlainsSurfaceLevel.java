/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.plains;

import java.awt.Point;
import java.util.ArrayList;
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
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.generationModules.LinesGeneration;
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

public class PlainsSurfaceLevel
extends Level {
    public PlainsSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public PlainsSurfaceLevel(int islandX, int islandY, float islandSize, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 90.0f) + 40;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.getTileID("watertile");
        int sandTile = TileRegistry.getTileID("sandtile");
        int grassTile = TileRegistry.plainsGrassID;
        int overgrownGrassTile = TileRegistry.overgrownPlainsGrassID;
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), e -> {
            if (ig.random.getChance(0.05f)) {
                ig.generateSimpleIsland(this.tileWidth / 2, this.tileHeight / 2, waterTile, grassTile, sandTile);
            } else {
                ig.generateShapedIsland(waterTile, grassTile, sandTile);
            }
            int rivers = ig.random.getIntBetween(1, 5);
            for (int i = 0; !(i >= rivers || i > 0 && ig.random.getChance(0.4f)); ++i) {
                ig.generateRiver(waterTile, grassTile, sandTile);
            }
            ig.generateLakes(0.02f, waterTile, grassTile, sandTile);
            ig.clearTinyIslands(waterTile);
            this.liquidManager.calculateFull();
        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), e -> {
            GenerationTools.generateRandomVeins(this, ig.random, 0.15f, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.7f) && this.getTileID(tileX, tileY) == grassTile) {
                    level.setTile(tileX, tileY, overgrownGrassTile);
                }
            });
            int oakTree = ObjectRegistry.getObjectID("mapletree");
            int spruceTree = ObjectRegistry.getObjectID("birchtree");
            int grassObject = ObjectRegistry.getObjectID("plainsgrass");
            int leafPileObject = ObjectRegistry.getObjectID("leafpile");
            ig.generateCellMapObjects(0.32f, oakTree, grassTile, 0.03f);
            ig.generateCellMapObjects(0.32f, spruceTree, grassTile, 0.05f);
            ig.generateObjects(grassObject, grassTile, 0.4f);
            ig.generateObjects(grassObject, overgrownGrassTile, 0.4f);
            ig.generateObjects(leafPileObject, grassTile, 0.01f);
            ig.generateObjects(leafPileObject, overgrownGrassTile, 0.01f);
            ig.generateObjects(ObjectRegistry.getObjectID("whiteflowerpatch"), grassTile, 0.005f);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerock"), -1, 0.001f, false);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerocksmall"), -1, 0.002f, false);
            ig.ensureGenerateObjects("beehive", 2, grassTile);
            ig.generateFruitGrowerVeins("raspberrybush", 0.04f, 8, 10, 0.1f, null, grassTile);
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03f, 5, 15, grassTile, ObjectRegistry.getObjectID("whiteflowerpatch"), 0.4f, false);
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.1f, 5, 10, grassTile, ObjectRegistry.getObjectID("wildsunflower"), 0.15f, false);
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
            float chanceToAddMiniBiome = 0.75f;
            TicketSystemList miniBiomesTicketList = new TicketSystemList();
            if (!this.baseBiome.hasVillage()) {
                miniBiomesTicketList.addObject(85, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new HunterCabinPreset(ig.random, furnitureSet, wallSet), 10, true, true);
                });
                miniBiomesTicketList.addObject(70, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick);
                    TreeSet treeSet = ig.random.getOneOf(TreeSet.birch, TreeSet.maple);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FarmersRefugePreset(ig.random, furnitureSet, wallSet, treeSet, BushSet.raspberry), 10, true, false, false);
                });
                miniBiomesTicketList.addObject(55, () -> {
                    FenceSet fenceSet = ig.random.getOneOf(FenceSet.wood, FenceSet.stone, FenceSet.iron);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new BrokenHusbandryFencePreset(ig.random, fenceSet), 10, true, true);
                });
                miniBiomesTicketList.addObject(55, () -> {
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick);
                    int tileID = ig.random.getOneOf(TileRegistry.woodPathID, TileRegistry.stonePathID);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FishingHutPreset(ig.random, wallSet, tileID), 10, true, true);
                });
                miniBiomesTicketList.addObject(60, () -> presets.findRandomValidPositionAndApply(ig.random, 200, new MerchantsAmbushPreset(ig.random, false), 10, true, true));
            }
            while (!miniBiomesTicketList.isEmpty() && ig.random.getChance(chanceToAddMiniBiome)) {
                Runnable miniBiome = (Runnable)miniBiomesTicketList.getAndRemoveRandomObject(ig.random);
                miniBiome.run();
                chanceToAddMiniBiome -= 0.15f;
            }
            for (int i = 0; i < 2; ++i) {
                presets.findRandomValidPositionAndApply(ig.random, 40, new RandomRuinsPreset(ig.random), 20, false, false);
            }
            if (ig.random.getChance(0.33f)) {
                int katanaAttempts = 50;
                for (int i = 0; i < katanaAttempts; ++i) {
                    int tileY;
                    int tileX = ig.random.getIntBetween(20, this.tileWidth - 20);
                    if (presets.isSpaceOccupied(tileX - 3, (tileY = ig.random.getIntBetween(20, this.tileHeight - 20)) - 3, 7, 7) || this.getTile((int)tileX, (int)tileY).isLiquid || this.liquidManager.isShore(tileX, tileY) || this.getTile((int)(tileX - 5), (int)tileY).isLiquid || this.getTile((int)(tileX + 5), (int)tileY).isLiquid || this.getTile((int)tileX, (int)(tileY - 5)).isLiquid || this.getTile((int)tileX, (int)(tileY + 5)).isLiquid) continue;
                    presets.addOccupiedSpace(tileX - 3, tileY - 3, 7, 7);
                    this.setObject(tileX, tileY, ObjectRegistry.getObjectID("katanastone"));
                    GameTile gravelTile = TileRegistry.getTile(TileRegistry.gravelID);
                    LinesGeneration lg = new LinesGeneration(tileX, tileY).addRandomArms(ig.random, 6, 6.0f, 9.0f, 2.0f, 3.0f);
                    CellAutomaton cellAutomaton = lg.doCellularAutomaton(ig.random);
                    cellAutomaton.forEachTile(this, (level, cellX, cellY) -> {
                        GameObject object = this.getObject(cellX, cellY);
                        if ((object.getID() == 0 || object.isGrass) && !level.getTile((int)cellX, (int)cellY).isLiquid && ig.random.getChance(0.3f)) {
                            gravelTile.placeTile(level, cellX, cellY, false);
                        }
                    });
                    ArrayList<Point> validStoneTiles = new ArrayList<Point>();
                    for (int x = -5; x <= 5; ++x) {
                        for (int y = -5; y <= 5; ++y) {
                            GameObject object = this.getObject(tileX + x, tileY + y);
                            if (object.getID() != 0 && !object.isGrass || this.getTile((int)(tileX + x), (int)(tileY + y)).isFloor) continue;
                            validStoneTiles.add(new Point(tileX + x, tileY + y));
                        }
                    }
                    GameObject stoneObject = ObjectRegistry.getObject("surfacerocksmall");
                    for (int j = 0; j < 10 && !validStoneTiles.isEmpty(); ++j) {
                        Point stoneTile = (Point)validStoneTiles.remove(ig.random.nextInt(validStoneTiles.size()));
                        stoneObject.placeObject(this, stoneTile.x, stoneTile.y, 0, false);
                    }
                    break;
                }
            }
            this.postGeneratedStructures(islandSize, ig, presets);
        });
        GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, islandSize, ig), e -> {
            ProtectedTicketSystemList sheepSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "sheep")).addObject(25, "ram");
            ig.spawnMobHerds((TicketSystemList<String>)sheepSpawns, ig.random.getIntBetween(25, 50), grassTile, 2, 6, islandSize);
            ProtectedTicketSystemList cowSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "cow")).addObject(25, "bull");
            ig.spawnMobHerds((TicketSystemList<String>)cowSpawns, ig.random.getIntBetween(15, 40), grassTile, 2, 6, islandSize);
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

