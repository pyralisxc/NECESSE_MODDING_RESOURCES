/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.forest;

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
import necesse.engine.network.server.Server;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.level.gameObject.GameObject;
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

public class ForestSurfaceLevel
extends Level {
    public ForestSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public ForestSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 90.0f) + 40;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.getTileID("watertile");
        int sandTile = TileRegistry.getTileID("sandtile");
        int grassTile = TileRegistry.grassID;
        int overgrownGrassTile = TileRegistry.overgrownGrassID;
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
            Point rndBlueBerryBushPos;
            GenerationTools.generateRandomVeins(this, ig.random, 0.15f, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.7f) && this.getTileID(tileX, tileY) == grassTile) {
                    level.setTile(tileX, tileY, overgrownGrassTile);
                }
            });
            int oakTree = ObjectRegistry.getObjectID("oaktree");
            int spruceTree = ObjectRegistry.getObjectID("sprucetree");
            int grassObject = ObjectRegistry.getObjectID("grass");
            ig.generateCellMapObjects(0.35f, oakTree, grassTile, 0.08f);
            ig.generateCellMapObjects(0.35f, spruceTree, grassTile, 0.12f);
            ig.generateObjects(grassObject, grassTile, 0.4f);
            ig.generateObjects(grassObject, overgrownGrassTile, 0.4f);
            ig.generateObjects(ObjectRegistry.getObjectID("oaktreestump"), true, grassTile, 6.0E-4f, true);
            ig.generateObjects(ObjectRegistry.getObjectID("sprucetreestump"), true, grassTile, 6.0E-4f, true);
            ig.generateObjects(ObjectRegistry.getObjectID("sprucelogbench"), true, grassTile, 6.0E-4f, true);
            ig.generateObjects(ObjectRegistry.getObjectID("yellowflowerpatch"), grassTile, 0.005f);
            ig.generateObjects(ObjectRegistry.getObjectID("redflowerpatch"), grassTile, 0.005f);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerock"), -1, 0.001f, false);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerocksmall"), -1, 0.002f, false);
            ig.ensureGenerateObjects("beehive", 1, grassTile);
            ig.generateFruitGrowerSingle("appletree", 0.02f, grassTile);
            ArrayList bushTiles = new ArrayList();
            ig.generateFruitGrowerVeins("blueberrybush", 0.04f, 8, 10, 0.1f, p -> {
                double centerDist = GameMath.diagonalMoveDistance(this.tileWidth / 2, this.tileHeight / 2, p.x, p.y);
                if (centerDist >= 40.0) {
                    bushTiles.add(p);
                }
            }, grassTile);
            if (ig.random.getEveryXthChance(2) && (rndBlueBerryBushPos = (Point)ig.random.getOneOf(bushTiles)) != null) {
                this.setObject(rndBlueBerryBushPos.x, rndBlueBerryBushPos.y, 0);
                Mob stabbyBush = MobRegistry.getMob("stabbybush", (Level)this);
                stabbyBush.resetUniqueID();
                stabbyBush.onSpawned(rndBlueBerryBushPos.x * 32 + 16, rndBlueBerryBushPos.y * 32 + 16);
                stabbyBush.canDespawn = false;
                this.entityManager.mobs.add(stabbyBush);
            }
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03f, 5, 15, grassTile, ObjectRegistry.getObjectID("yellowflowerpatch"), 0.4f, false);
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03f, 5, 15, grassTile, ObjectRegistry.getObjectID("redflowerpatch"), 0.4f, false);
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03f, 6, 12, grassTile, ObjectRegistry.getObjectID("wildfiremone"), 0.2f, false);
            GameObject waterPlant = ObjectRegistry.getObject(ObjectRegistry.getObjectID("watergrass"));
            GenerationTools.generateRandomVeins(this, ig.random, 0.2f, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.3f) && waterPlant.canPlace(level, tileX, tileY, 0, false) == null && level.liquidManager.isFreshWater(tileX, tileY)) {
                    waterPlant.placeObject(level, tileX, tileY, 0, false);
                }
            });
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), e -> {
            PresetGeneration presets = new PresetGeneration(this);
            this.preGeneratedStructures(islandSize, ig, presets);
            presets.findRandomValidPositionAndApply(ig.random, 40, new RandomRuinsPreset(ig.random), 20, false, false);
            float chanceToAddMiniBiome = 0.75f;
            TicketSystemList miniBiomesTicketList = new TicketSystemList();
            if (!this.baseBiome.hasVillage()) {
                miniBiomesTicketList.addObject(85, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.spruce, FurnitureSet.pine, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new HunterCabinPreset(ig.random, furnitureSet, wallSet), 10, true, true);
                });
                int tileID = ig.random.getOneOf(TileRegistry.woodPathID, TileRegistry.stonePathID);
                miniBiomesTicketList.addObject(70, () -> {
                    FurnitureSet furnitureSet = ig.random.getOneOf(FurnitureSet.spruce, FurnitureSet.pine, FurnitureSet.oak);
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick);
                    TreeSet treeSet = ig.random.getOneOf(TreeSet.oak, TreeSet.spruce);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new FarmersRefugePreset(ig.random, furnitureSet, wallSet, treeSet, BushSet.blueberry), 10, true, false, false);
                });
                miniBiomesTicketList.addObject(50, () -> {
                    FenceSet fenceSet = ig.random.getOneOf(FenceSet.wood, FenceSet.stone, FenceSet.iron);
                    presets.findRandomValidPositionAndApply(ig.random, 200, new BrokenHusbandryFencePreset(ig.random, fenceSet), 10, true, true);
                });
                miniBiomesTicketList.addObject(50, () -> {
                    WallSet wallSet = ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick);
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
            ig.spawnMobHerds((TicketSystemList<String>)sheepSpawns, ig.random.getIntBetween(25, 45), grassTile, 2, 6, islandSize);
            ProtectedTicketSystemList cowSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "cow")).addObject(25, "bull");
            ig.spawnMobHerds((TicketSystemList<String>)cowSpawns, ig.random.getIntBetween(15, 35), grassTile, 2, 6, islandSize);
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

