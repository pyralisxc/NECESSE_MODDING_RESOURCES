/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.forest;

import java.awt.Color;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.biomeGenerator.GeneratorPlaceFactory;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;
import necesse.level.maps.regionSystem.Region;

public class ForestBiome
extends Biome {
    public static MobSpawnTable caveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "stonecaveling");
    public static MobSpawnTable deepCaveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "deepstonecaveling");
    public static MobSpawnTable deepCaveMobs = new MobSpawnTable().include(Biome.defaultDeepCaveMobs).add(45, "flamelingshooter");
    public static FishingLootTable forestSurfaceFish = new FishingLootTable(defaultSurfaceFish).addWater(120, "furfish");
    public static LootItemInterface randomPortalDrop = new LootItemList(new ChanceLootItem(0.01f, "mysteriousportal"));
    public static LootItemInterface randomShadowGateDrop = new LootItemList(new ChanceLootItem(0.004f, "shadowgate"));

    @Override
    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new ForestSurfaceLevel(islandX, islandY, islandSize, server, worldEntity, this);
    }

    @Override
    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new ForestCaveLevel(islandX, islandY, dimension, worldEntity, this);
    }

    @Override
    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new ForestDeepCaveLevel(islandX, islandY, dimension, worldEntity, this);
    }

    @Override
    public MobSpawnTable getCritterSpawnTable(Level level) {
        if (level.isCave) {
            if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return deepCaveCritters;
            }
            return caveCritters;
        }
        return super.getCritterSpawnTable(level);
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return defaultSurfaceMobs;
        }
        if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return deepCaveMobs;
        }
        return defaultCaveMobs;
    }

    @Override
    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        if (!spot.tile.level.isCave) {
            return forestSurfaceFish;
        }
        return super.getFishingLootTable(spot);
    }

    @Override
    public LootTable getExtraMobDrops(Mob mob) {
        if (mob.isHostile && !mob.isBoss() && !mob.isSummoned) {
            if (mob.getLevel().getIdentifier().equals(LevelIdentifier.CAVE_IDENTIFIER)) {
                return new LootTable(randomPortalDrop, super.getExtraMobDrops(mob));
            }
            if (mob.getLevel().getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new LootTable(randomShadowGateDrop, super.getExtraMobDrops(mob));
            }
        }
        return super.getExtraMobDrops(mob);
    }

    @Override
    public LootTable getExtraBiomeMobDrops(LevelIdentifier levelIdentifier) {
        if (levelIdentifier == null) {
            return new LootTable();
        }
        if (levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            return new LootTable(randomPortalDrop);
        }
        if (levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return new LootTable(randomShadowGateDrop);
        }
        return new LootTable();
    }

    @Override
    public GameTile getUnderLiquidTile(Level level, int tileX, int tileY) {
        if (level.isCave) {
            return TileRegistry.getTile(TileRegistry.dirtID);
        }
        return TileRegistry.getTile(TileRegistry.sandID);
    }

    @Override
    public int getGenerationTerrainTileID() {
        return TileRegistry.grassID;
    }

    @Override
    public void initializeGeneratorStack(BiomeGeneratorStack stack) {
        super.initializeGeneratorStack(stack);
        stack.addRandomSimplexVeinsBranch("forestOvergrownGrass", 2.5f, 0.45f, 0.7f, 2);
        stack.addRandomSimplexVeinsBranch("forestOakTrees", 2.0f, 0.2f, 1.0f, 0);
        stack.addRandomSimplexVeinsBranch("forestSpruceTrees", 2.0f, 0.2f, 1.0f, 0);
        stack.addRandomVeinsBranch("forestYellowFlowerPatch", 0.05f, 5, 15, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestRedFlowerPatch", 0.05f, 5, 15, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestFiremonePatch", 0.05f, 6, 12, 0.4f, 2, false);
        stack.addRandomSimplexVeinsBranch("forestWaterGrass", 2.0f, 0.33f, 1.0f, 0);
        stack.addRandomVeinsBranch("forestBlueberries", 0.065f, 8, 10, 0.1f, 0, false);
        stack.addRandomVeinsBranch("forestCows", 0.02f, 8, 12, 0.1f, 0, false);
        stack.addRandomVeinsBranch("forestSheep", 0.02f, 8, 12, 0.1f, 0, false);
        stack.addRandomVeinsBranch("forestClay", 0.4f, 5, 10, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestCopper", 0.72f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestIron", 0.56f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestGold", 0.16f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestWildCaveGlow", 0.32f, 4, 8, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestDeepCopper", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestDeepIron", 0.4f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestDeepGold", 0.24f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestDeepObsidian", 0.4f, 5, 10, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestDeepTungsten", 0.32f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("forestDeepLifeQuartz", 0.08f, 3, 6, 0.4f, 2, false);
    }

    @Override
    public void generateRegionSurfaceTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionSurfaceTerrain(region, stack, random);
        int grassTile = TileRegistry.grassID;
        stack.startPlaceOnVein(this, region, random, "forestOvergrownGrass").onlyOnTile(grassTile).chance(0.7f).placeTile(TileRegistry.overgrownGrassID);
        stack.startPlaceOnVein(this, region, random, "forestOakTrees").onlyOnTile(grassTile).chance(0.08f).placeObject("oaktree");
        stack.startPlaceOnVein(this, region, random, "forestSpruceTrees").onlyOnTile(grassTile).chance(0.12f).placeObject("sprucetree");
        stack.startPlace(this, region, random).chance(0.4f).testTileID(tileID -> tileID == grassTile || tileID == TileRegistry.overgrownGrassID).placeObject("grass");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(6.0E-4f).placeObjectRandomRotation("oaktreestump");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(6.0E-4f).placeObjectRandomRotation("sprucetreestump");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(6.0E-4f).placeObjectRandomRotation("sprucelogbench");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(0.005f).placeObject("yellowflowerpatch");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(0.005f).placeObject("redflowerpatch");
        stack.startPlace(this, region, random).chance(0.0015f).placeObject("surfacerock");
        stack.startPlace(this, region, random).chance(0.0025f).placeObject("surfacerocksmall");
        stack.startPlace(this, region, random).chancePerRegion(0.02f).onlyOnTile(grassTile).placeObject("beehive");
        stack.startPlace(this, region, random).chancePerRegion(0.035f).onlyOnTile(grassTile).placeObjectFruitGrower("appletree");
        stack.startPlaceOnVein(this, region, random, "forestBlueberries").onlyOnTile(grassTile).placeObjectEntity("blueberrybush", FruitGrowerObjectEntity.class, (r, entity) -> {
            if (r.getChance(0.01f)) {
                Level level = entity.getLevel();
                level.setObject(entity.tileX, entity.tileY, 0);
                Mob stabbyBush = MobRegistry.getMob("stabbybush", level);
                stabbyBush.resetUniqueID(random);
                stabbyBush.onSpawned(entity.tileX * 32 + 16, entity.tileY * 32 + 16);
                stabbyBush.canDespawn = false;
                level.entityManager.mobs.add(stabbyBush);
            } else {
                entity.setRandomStage((GameRandom)r);
            }
        });
        stack.startPlaceOnVein(this, region, random, "forestYellowFlowerPatch").onlyOnTile(grassTile).chance(0.4f).placeObject("yellowflowerpatch");
        stack.startPlaceOnVein(this, region, random, "forestRedFlowerPatch").onlyOnTile(grassTile).chance(0.4f).placeObject("redflowerpatch");
        stack.startPlaceOnVein(this, region, random, "forestFiremonePatch").onlyOnTile(grassTile).chance(0.2f).placeObject("wildfiremone");
        ProtectedTicketSystemList cowSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "cow")).addObject(25, "bull");
        stack.startPlaceOnVein(this, region, random, "forestCows").onlyOnTile(grassTile).placeMob((TicketSystemList<String>)cowSpawns);
        ProtectedTicketSystemList sheepSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "sheep")).addObject(25, "ram");
        stack.startPlaceOnVein(this, region, random, "forestSheep").onlyOnTile(grassTile).placeMob((TicketSystemList<String>)sheepSpawns);
        region.updateLiquidManager();
        stack.startPlaceOnVein(this, region, random, "forestWaterGrass").chance(0.3f).test(new GeneratorPlaceFactory.RegionCanPlaceFunction(){

            @Override
            public boolean canPlace(GameRandom random, Region region, int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                return region.liquidData.isFreshWaterByRegion(regionTileX, regionTileY);
            }
        }).placeObject("watergrass");
    }

    @Override
    public void generateRegionCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("caverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("caverocksmall");
        stack.startPlace(this, region, random).chance(0.03f).placeCrates("crate");
        stack.startPlaceOnVein(this, region, random, "forestClay").onlyOnObject(ObjectRegistry.rockID).placeObjectForced("clayrock");
        stack.startPlaceOnVein(this, region, random, "forestCopper").onlyOnObject(ObjectRegistry.rockID).placeObjectForced("copperorerock");
        stack.startPlaceOnVein(this, region, random, "forestIron").onlyOnObject(ObjectRegistry.rockID).placeObjectForced("ironorerock");
        stack.startPlaceOnVein(this, region, random, "forestGold").onlyOnObject(ObjectRegistry.rockID).placeObjectForced("goldorerock");
    }

    @Override
    public void generateRegionDeepCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionDeepCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("deepcaverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("deepcaverocksmall");
        stack.startPlace(this, region, random).chance(0.03f).placeCrates("crate");
        stack.startPlaceOnVein(this, region, random, "forestWildCaveGlow").onlyOnTile(TileRegistry.deepRockID).chance(0.2f).placeObject("wildcaveglow");
        stack.startPlaceOnVein(this, region, random, "forestDeepCopper").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("copperoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepIron").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("ironoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepGold").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("goldoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepObsidian").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("obsidianrock");
        stack.startPlaceOnVein(this, region, random, "forestDeepTungsten").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("tungstenoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepLifeQuartz").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("lifequartzdeeprock");
    }

    @Override
    public Color getDebugBiomeColor() {
        return new Color(28, 115, 0);
    }
}

