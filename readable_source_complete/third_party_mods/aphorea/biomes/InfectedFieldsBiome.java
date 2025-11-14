/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.AbstractMusicList
 *  necesse.engine.MusicList
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.registries.MusicRegistry
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.registries.TileRegistry
 *  necesse.engine.sound.GameMusic
 *  necesse.engine.util.GameRandom
 *  necesse.engine.world.biomeGenerator.BiomeGeneratorStack
 *  necesse.entity.mobs.PlayerMob
 *  necesse.level.gameTile.GameTile
 *  necesse.level.maps.Level
 *  necesse.level.maps.biomes.Biome
 *  necesse.level.maps.biomes.FishingLootTable
 *  necesse.level.maps.biomes.FishingSpot
 *  necesse.level.maps.biomes.MobSpawnTable
 *  necesse.level.maps.presets.RandomCaveChestRoom
 *  necesse.level.maps.presets.caveRooms.CaveRuins
 *  necesse.level.maps.regionSystem.Region
 */
package aphorea.biomes;

import aphorea.registry.AphObjects;
import aphorea.registry.AphTiles;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.GameMusic;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.entity.mobs.PlayerMob;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.regionSystem.Region;

public class InfectedFieldsBiome
extends Biome {
    public static FishingLootTable surfaceFish;
    public static MobSpawnTable surfaceMobs;
    public static MobSpawnTable caveMobs;
    public static MobSpawnTable deepCaveMobs;
    public static MobSpawnTable surfaceCritters;
    public static MobSpawnTable caveCritters;

    public boolean canRain(Level level) {
        return false;
    }

    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        return !spot.tile.level.isCave ? surfaceFish : super.getFishingLootTable(spot);
    }

    public MobSpawnTable getCritterSpawnTable(Level level) {
        return !level.isCave ? surfaceCritters : caveCritters;
    }

    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        }
        return level.getIslandDimension() == -2 ? deepCaveMobs : caveMobs;
    }

    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        return new MusicList(new GameMusic[]{MusicRegistry.GrindTheAlarms});
    }

    public GameTile getUnderLiquidTile(Level level, int tileX, int tileY) {
        return level.isCave ? TileRegistry.getTile((int)TileRegistry.dirtID) : TileRegistry.getTile((int)TileRegistry.sandID);
    }

    public int getGenerationWaterTileID() {
        return AphTiles.INFECTED_WATER;
    }

    public int getGenerationCaveLavaTileID() {
        return AphTiles.INFECTED_WATER;
    }

    public int getGenerationDeepCaveLavaTileID() {
        return AphTiles.INFECTED_WATER;
    }

    public int getGenerationTerrainTileID() {
        return AphTiles.INFECTED_GRASS;
    }

    public int getGenerationCaveRockObjectID() {
        return AphObjects.GEL_ROCK;
    }

    public int getGenerationCaveTileID() {
        return super.getGenerationCaveTileID();
    }

    public void initializeGeneratorStack(BiomeGeneratorStack stack) {
        super.initializeGeneratorStack(stack);
        stack.addRandomSimplexVeinsBranch("infectedTrees", 2.0f, 0.2f, 1.0f, 0);
        stack.addRandomVeinsBranch("infectedTungsten", 0.16f, 3, 6, 0.4f, 2, false);
    }

    public void generateRegionSurfaceTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionSurfaceTerrain(region, stack, random);
        int grassTile = AphTiles.INFECTED_GRASS;
        stack.startPlaceOnVein((Biome)this, region, random, "infectedTrees").onlyOnTile(grassTile).chance((double)0.08f).placeObject("infectedtree");
        stack.startPlace((Biome)this, region, random).onlyOnTile(grassTile).chance((double)0.4f).placeObject("infectedgrass");
        stack.startPlace((Biome)this, region, random).chance((double)0.0025f).placeObject("surfacegelrock");
        region.updateLiquidManager();
    }

    public void generateRegionCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionCaveTerrain(region, stack, random);
        stack.startPlace((Biome)this, region, random).chance((double)0.005f).placeObject("spinelcluster");
        stack.startPlace((Biome)this, region, random).chance((double)0.01f).placeObject("spinelclustersmall");
        stack.startPlaceOnVein((Biome)this, region, random, "infectedTungsten").onlyOnObject(AphObjects.GEL_ROCK).placeObjectForced("tungstenoregelrock");
    }

    public void generateRegionDeepCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionDeepCaveTerrain(region, stack, random);
        stack.startPlace((Biome)this, region, random).chance((double)0.005f).placeObject("deepcaverock");
        stack.startPlace((Biome)this, region, random).chance((double)0.01f).placeObject("deepcaverocksmall");
        stack.startPlace((Biome)this, region, random).chance((double)0.03f).placeCrates(new String[]{"crate"});
        stack.startPlaceOnVein((Biome)this, region, random, "forestWildCaveGlow").onlyOnTile(TileRegistry.deepRockID).chance((double)0.2f).placeObject("wildcaveglow");
        stack.startPlaceOnVein((Biome)this, region, random, "forestDeepCopper").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("copperoredeeprock");
        stack.startPlaceOnVein((Biome)this, region, random, "forestDeepIron").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("ironoredeeprock");
        stack.startPlaceOnVein((Biome)this, region, random, "forestDeepGold").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("goldoredeeprock");
        stack.startPlaceOnVein((Biome)this, region, random, "forestDeepObsidian").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("obsidianrock");
        stack.startPlaceOnVein((Biome)this, region, random, "forestDeepTungsten").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("tungstenoredeeprock");
        stack.startPlaceOnVein((Biome)this, region, random, "forestDeepLifeQuartz").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("lifequartzdeeprock");
    }

    public Color getDebugBiomeColor() {
        return AphColors.spinel;
    }

    public CaveRuins getNewCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    public CaveRuins getNewDeepCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    public RandomCaveChestRoom getNewCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    public RandomCaveChestRoom getNewDeepCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    static {
        surfaceMobs = new MobSpawnTable().addLimited(100, "infectedtreant", 10, 3200).add(20, "rockygelslime").add(1, "stabbybush");
        caveMobs = new MobSpawnTable();
        deepCaveMobs = new MobSpawnTable();
        int woodTrashTickets = 20;
        surfaceFish = new FishingLootTable().startCustom(300).onlyTile("infectedwatertile").end("rockfish").startCustom(100).onlyTile("infectedwatertile").end("fossilrapier").startCustom(400).onlyTile("infectedwatertile").end((spot, random) -> ItemRegistry.getItem((String)"infectedlog").getDefaultLootItem(random, random.getIntBetween(1, 3))).startCustom(20).onlyTile("infectedwatertile").end("woodaxe").startCustom(20).onlyTile("infectedwatertile").end("woodpickaxe").startCustom(20).onlyTile("infectedwatertile").end("woodshovel").startCustom(20).onlyTile("infectedwatertile").end("woodfishingrod").startCustom(20).onlyTile("infectedwatertile").end("woodsword").startCustom(20).onlyTile("infectedwatertile").end("woodspear").startCustom(20).onlyTile("infectedwatertile").end("woodboomerang").startCustom(20).onlyTile("infectedwatertile").end("woodbow").startCustom(20).onlyTile("infectedwatertile").end("woodstaff").startCustom(20).onlyTile("infectedwatertile").end("woodshield");
        surfaceCritters = new MobSpawnTable();
        caveCritters = new MobSpawnTable();
    }
}

