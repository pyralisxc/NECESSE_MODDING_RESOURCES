/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.desert;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.biomeGenerator.GeneratorPlaceFactory;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.desert.DesertCaveLevel;
import necesse.level.maps.biomes.desert.DesertDeepCaveLevel;
import necesse.level.maps.biomes.desert.DesertSurfaceLevel;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.VillageSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.regionSystem.Region;

public class DesertBiome
extends Biome {
    public static MobSpawnTable surfaceMobs = new MobSpawnTable().include(defaultSurfaceMobs).add(35, "mummy");
    public static MobSpawnTable caveMobs = new MobSpawnTable().add(80, "mummy").add(40, "mummymage").add(40, "sandspirit").add(80, "jackal");
    public static MobSpawnTable deepDesertCaveMobs = new MobSpawnTable().add(100, "ancientskeleton").add(40, "ancientskeletonthrower").add(50, "desertcrawler").addLimited(6, "sandworm", 1, Mob.MOB_SPAWN_AREA.maxSpawnDistance * 2);
    public static MobSpawnTable surfaceCritters = new MobSpawnTable().add(100, "crab").add(60, "scorpion").add(60, "canarybird").add(20, "bird").add(60, "turtle").add(10, "duck");
    public static MobSpawnTable caveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "sandstonecaveling");
    public static MobSpawnTable deepCaveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "deepsandstonecaveling");
    public static LootItemInterface randomAncientStatueDrop = new LootItemList(new ChanceLootItem(0.005f, "ancientstatue"));
    public static LootItemInterface dragonSoulsDrop = new LootItemList(new ChanceLootItem(0.004f, "dragonsouls"));

    @Override
    public SoundSettings getWindSound(Level level) {
        return SoundSettingsRegistry.windDesert;
    }

    @Override
    public boolean canRain(Level level) {
        return false;
    }

    @Override
    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new DesertSurfaceLevel(islandX, islandY, islandSize, worldEntity, this);
    }

    @Override
    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new DesertCaveLevel(islandX, islandY, dimension, worldEntity, (Biome)this);
    }

    @Override
    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new DesertDeepCaveLevel(islandX, islandY, dimension, worldEntity, (Biome)this);
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        }
        if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return deepDesertCaveMobs;
        }
        return caveMobs;
    }

    @Override
    public MobSpawnTable getCritterSpawnTable(Level level) {
        if (level.isCave) {
            if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return deepCaveCritters;
            }
            return caveCritters;
        }
        return surfaceCritters;
    }

    @Override
    public LootTable getExtraMobDrops(Mob mob) {
        if (mob.isHostile && !mob.isBoss() && !mob.isSummoned) {
            if (mob.getLevel().getIdentifier().equals(LevelIdentifier.CAVE_IDENTIFIER)) {
                return new LootTable(randomAncientStatueDrop, super.getExtraMobDrops(mob));
            }
            if (mob.getLevel().getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new LootTable(dragonSoulsDrop, super.getExtraMobDrops(mob));
            }
        }
        return super.getExtraMobDrops(mob);
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new MusicList(MusicRegistry.SandCatacombs);
            }
            return new MusicList(MusicRegistry.DustyHollows);
        }
        if (level.getWorldEntity().isNight()) {
            return new MusicList(MusicRegistry.NightInTheDunes);
        }
        return new MusicList(MusicRegistry.OasisSerenade);
    }

    @Override
    public LootTable getExtraBiomeMobDrops(LevelIdentifier levelIdentifier) {
        if (levelIdentifier == null) {
            return new LootTable();
        }
        if (levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            return new LootTable(randomAncientStatueDrop);
        }
        if (levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return new LootTable(dragonSoulsDrop);
        }
        return new LootTable();
    }

    @Override
    public GameTile getUnderLiquidTile(Level level, int tileX, int tileY) {
        return TileRegistry.getTile(TileRegistry.sandID);
    }

    @Override
    public RandomCaveChestRoom getNewCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.desertCaveChest, lootRotation, ChestRoomSet.sandstone, ChestRoomSet.wood);
        chestRoom.replaceTile(TileRegistry.stoneFloorID, random.getOneOf(TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID));
        chestRoom.replaceTile(TileRegistry.sandstoneFloorID, random.getOneOf(TileRegistry.sandstoneFloorID, TileRegistry.sandstoneBrickFloorID));
        return chestRoom;
    }

    @Override
    public RandomCaveChestRoom getNewDeepCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.deepDesertCaveChest, lootRotation, ChestRoomSet.deepSandstone, ChestRoomSet.obsidian);
        chestRoom.replaceTile(TileRegistry.deepStoneFloorID, random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
        return chestRoom;
    }

    @Override
    public CaveRuins getNewCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.sandstone, WallSet.wood);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.palm, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("woodfloor", "woodfloor", "sandstonefloor", "sandstonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.desertCaveRuinsChest, lootRotation);
    }

    @Override
    public CaveRuins getNewDeepCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.deepSandstone, WallSet.obsidian);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.palm, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("deepstonefloor", "deepstonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.desertDeepCaveRuinsChest, lootRotation);
    }

    @Override
    public int getGenerationTerrainTileID() {
        return TileRegistry.sandID;
    }

    @Override
    public int getGenerationCaveTileID() {
        return TileRegistry.sandstoneID;
    }

    @Override
    public int getGenerationCaveRockObjectID() {
        return ObjectRegistry.sandstoneRockID;
    }

    @Override
    public int getGenerationDeepCaveTileID() {
        return TileRegistry.deepSandstoneID;
    }

    @Override
    public int getGenerationDeepCaveRockObjectID() {
        return ObjectRegistry.deepSandstoneRockID;
    }

    @Override
    public VillageSet[] getVillageSets() {
        return new VillageSet[]{VillageSet.palm};
    }

    @Override
    public void initializeGeneratorStack(BiomeGeneratorStack stack) {
        super.initializeGeneratorStack(stack);
        stack.addRandomSimplexVeinsBranch("desertWaterGrass", 2.0f, 0.33f, 1.0f, 0);
        stack.addRandomVeinsBranch("desertQuartz", 0.3f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertCopper", 0.3f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertIron", 0.25f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertGold", 0.15f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertDeepCopper", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertDeepIron", 0.4f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertDeepGold", 0.24f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertDeepAncientFossil", 0.272f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("desertDeepLifeQuartz", 0.08f, 3, 6, 0.4f, 2, false);
    }

    @Override
    public void generateRegionSurfaceTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionSurfaceTerrain(region, stack, random);
        int sandTile = TileRegistry.sandID;
        stack.startPlace(this, region, random).chance(0.0015f).placeObject("sandsurfacerock");
        stack.startPlace(this, region, random).chance(0.0025f).placeObject("sandsurfacerocksmall");
        stack.startPlace(this, region, random).chance(0.01f).onlyOnTile(sandTile).placeObject("cactus");
        stack.startPlace(this, region, random).chance(0.002f).onlyOnTile(sandTile).placeObject("palmtree");
        stack.startPlace(this, region, random).chancePerRegion(0.05f).onlyOnTile(sandTile).placeObjectFruitGrower("coconuttree");
        stack.startPlace(this, region, random).chance(4.0E-4f).onlyOnTile(sandTile).placeObject("cowskeleton");
        stack.startPlace(this, region, random).onlyOnTile(sandTile).chancePerRegion(0.02f).placeMob("wildostrich");
        region.updateLiquidManager();
        stack.startPlaceOnVein(this, region, random, "desertWaterGrass").chance(0.3f).test(new GeneratorPlaceFactory.RegionCanPlaceFunction(){

            @Override
            public boolean canPlace(GameRandom random, Region region, int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                return region.liquidData.isFreshWaterByRegion(regionTileX, regionTileY);
            }
        }).placeObject("watergrass");
    }

    @Override
    public void generateRegionCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("sandcaverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("sandcaverocksmall");
        stack.startPlace(this, region, random).chance(0.04f).placeCrates("crate", "vase");
        stack.startPlaceOnVein(this, region, random, "desertQuartz").onlyOnObject(ObjectRegistry.sandstoneRockID).placeObjectForced("quartzsandstone");
        stack.startPlaceOnVein(this, region, random, "desertCopper").onlyOnObject(ObjectRegistry.sandstoneRockID).placeObjectForced("copperoresandstone");
        stack.startPlaceOnVein(this, region, random, "desertIron").onlyOnObject(ObjectRegistry.sandstoneRockID).placeObjectForced("ironoresandstone");
        stack.startPlaceOnVein(this, region, random, "desertGold").onlyOnObject(ObjectRegistry.sandstoneRockID).placeObjectForced("goldoresandstone");
    }

    @Override
    public void generateRegionDeepCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionDeepCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("deepsandcaverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("deepsandcaverocksmall");
        stack.startPlace(this, region, random).chance(0.04f).placeCrates("crate", "vase");
        stack.startPlaceOnVein(this, region, random, "desertDeepCopper").onlyOnObject(ObjectRegistry.deepSandstoneRockID).placeObjectForced("copperoredeepsandstonerock");
        stack.startPlaceOnVein(this, region, random, "desertDeepIron").onlyOnObject(ObjectRegistry.deepSandstoneRockID).placeObjectForced("ironoredeepsandstonerock");
        stack.startPlaceOnVein(this, region, random, "desertDeepGold").onlyOnObject(ObjectRegistry.deepSandstoneRockID).placeObjectForced("goldoredeepsandstonerock");
        stack.startPlaceOnVein(this, region, random, "desertDeepAncientFossil").onlyOnObject(ObjectRegistry.deepSandstoneRockID).placeObjectForced("ancientfossiloredeepsnowrock");
        stack.startPlaceOnVein(this, region, random, "desertDeepLifeQuartz").onlyOnObject(ObjectRegistry.deepSandstoneRockID).placeObjectForced("lifequartzdeepsandstonerock");
    }

    @Override
    public Color getDebugBiomeColor() {
        return new Color(210, 176, 114);
    }
}

