/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldGenerator;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.BasicCaveLevel;
import necesse.level.maps.biomes.BasicDeepCaveLevel;
import necesse.level.maps.biomes.BasicSurfaceLevel;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.desert.DesertBiome;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.biomes.plains.PlainsBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.VillageSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.regionSystem.Region;

public class Biome {
    public static SharedGameTexture rainTextures;
    public static GameTexture generatedRainTexture;
    public static final int PRIORITY_BLENDING_BASE = 0;
    public static final int PRIORITY_BLENDING_ABOVE = 100;
    public static final int PRIORITY_BLENDING_TOP = 200;
    public final IDData idData = new IDData();
    public static MobSpawnTable defaultSurfaceMobs;
    public static MobSpawnTable defaultCaveMobs;
    public static MobSpawnTable forestCaveMobs;
    public static MobSpawnTable defaultDeepCaveMobs;
    public static FishingLootTable commonFish;
    public static FishingLootTable defaultSurfaceFish;
    public static FishingLootTable defaultCaveFish;
    public static MobSpawnTable defaultSurfaceCritters;
    public static MobSpawnTable defaultCaveCritters;
    public GameTexture iconTexture;
    public GameTextureSection rainTexture;
    protected GameMessage displayName;
    protected float generationWeight = 0.0f;

    public static void setupBiomeTextures() {
        rainTextures = new SharedGameTexture("rainShared");
    }

    public static void generateBiomeTextures() {
        generatedRainTexture = rainTextures.generate();
        rainTextures.close();
    }

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public Biome() {
        if (BiomeRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct Biome objects when biome registry is closed, since they are a static registered objects. Use BiomeRegistry.getBiome(...) to get biomes.");
        }
    }

    public void onBiomeRegistryClosed() {
    }

    public GameMessage getNewLocalization() {
        return new LocalMessage("biome", this.getStringID());
    }

    public void updateLocalDisplayName() {
        this.displayName = this.getNewLocalization();
    }

    public GameMessage getLocalization() {
        return this.displayName;
    }

    protected void loadIconTexture() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("biomes/" + this.getStringID());
        }
        catch (FileNotFoundException e) {
            this.iconTexture = null;
        }
    }

    protected void loadRainTexture() {
        this.rainTexture = rainTextures.addTexture(GameTexture.fromFile("rainfall"));
    }

    public final void loadTextures() {
        this.loadIconTexture();
        this.loadRainTexture();
    }

    public GameTexture getIconTexture(boolean isHovering) {
        if (this.iconTexture == null) {
            return isHovering ? Settings.UI.biome_unknown.highlighted : Settings.UI.biome_unknown.active;
        }
        return this.iconTexture;
    }

    public GameTextureSection getRainTexture(Level level, int tileX, int tileY) {
        return this.rainTexture;
    }

    public String getDisplayName() {
        return this.displayName.translate();
    }

    public boolean canRain(Level level) {
        return !level.isCave;
    }

    public float getWindModifier(Level level, int tileX, int tileY) {
        if (level.isCave) {
            return 0.0f;
        }
        if (!level.isOutside(tileX, tileY)) {
            return 0.0f;
        }
        return 1.0f;
    }

    public double getWindProgressDivider(Level level) {
        return 500.0;
    }

    public float getWindSpeedParticleLimit(Level level) {
        return 0.7f;
    }

    public float getWindAmountParticleLimit(Level level) {
        return 0.3f;
    }

    public float getWindParticleBufferModifier(Level level) {
        return 40.0f;
    }

    public Color getWindColor(Level level) {
        return new Color(1.0f, 1.0f, 1.0f);
    }

    public SoundSettings getWindSound(Level level) {
        return SoundSettingsRegistry.wind;
    }

    public int getRainTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(240, 420);
    }

    public int getDryTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(1200, 1800);
    }

    public Color getRainColor(Level level, int tileX, int tileY) {
        return new Color(50, 50, 200, 75);
    }

    public void tickRainEffect(GameCamera camera, Level level, int tileX, int tileY, float rainAlpha) {
        if (GameRandom.globalRandom.nextInt(60) == 0) {
            int spriteRes = 20;
            Color rainColor = this.getRainColor(level, tileX, tileY);
            Color particleColor = new Color(rainColor.getRed(), rainColor.getGreen(), rainColor.getBlue(), (int)((float)rainColor.getAlpha() * rainAlpha));
            level.entityManager.addParticle(ParticleOption.base(tileX * 32 + GameRandom.globalRandom.nextInt(32 - spriteRes), tileY * 32 + GameRandom.globalRandom.nextInt(32 - spriteRes)), Particle.GType.COSMETIC).lifeTime(600).sprite((options, lifeTime, timeAlive, lifePercent) -> {
                int frames = GameResources.rainBlobParticle.getWidth() / spriteRes;
                return options.add(GameResources.rainBlobParticle.sprite(Math.min((int)(lifePercent * (float)frames), frames - 1), 0, spriteRes));
            }).color(particleColor);
        }
    }

    public GameSound getRainSound(Level level) {
        return GameRandom.globalRandom.getOneOf(GameResources.rain1, GameResources.rain2, GameResources.rain3, GameResources.rain4, GameResources.rain5);
    }

    public GameTile getUnderLiquidTile(Level level, int tileX, int tileY) {
        return TileRegistry.getTile(TileRegistry.dirtID);
    }

    public boolean hasVillage() {
        return false;
    }

    public Level getNewLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        if (dimension == 0) {
            return this.getNewSurfaceLevel(islandX, islandY, server, worldEntity);
        }
        if (dimension == -1) {
            return this.getNewCaveLevel(islandX, islandY, dimension, server, worldEntity);
        }
        if (dimension < -1) {
            return this.getNewDeepCaveLevel(islandX, islandY, dimension, server, worldEntity);
        }
        return null;
    }

    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new BasicSurfaceLevel(islandX, islandY, islandSize, worldEntity, this);
    }

    public final Level getNewSurfaceLevel(int islandX, int islandY, Server server, WorldEntity worldEntity) {
        return this.getNewSurfaceLevel(islandX, islandY, WorldGenerator.getIslandSize(islandX, islandY), server, worldEntity);
    }

    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new BasicCaveLevel(islandX, islandY, dimension, worldEntity);
    }

    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new BasicDeepCaveLevel(islandX, islandY, dimension, worldEntity);
    }

    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return defaultSurfaceMobs;
        }
        if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return defaultDeepCaveMobs;
        }
        return forestCaveMobs;
    }

    public MobSpawnTable getCritterSpawnTable(Level level) {
        if (!level.isCave) {
            return defaultSurfaceCritters;
        }
        return defaultCaveCritters;
    }

    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        if (!spot.tile.level.isCave) {
            return defaultSurfaceFish;
        }
        return defaultCaveFish;
    }

    public LootTable getExtraMobDrops(Mob mob) {
        return new LootTable();
    }

    public LootTable getExtraBiomeMobDrops(LevelIdentifier levelIdentifier) {
        return new LootTable();
    }

    public LootTable getExtraPrivateMobDrops(Mob mob, ServerClient client) {
        return new LootTable();
    }

    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new MusicList(MusicRegistry.SecretsOfTheForest);
            }
            return new MusicList(MusicRegistry.DepthsOfTheForest);
        }
        if (level.getWorldEntity().isNight()) {
            return new MusicList(MusicRegistry.AwakeningTwilight);
        }
        return new MusicList(MusicRegistry.ForestPath);
    }

    public float getSpawnRateMod(Level level) {
        return 1.0f;
    }

    public float getSpawnCapMod(Level level) {
        return 1.0f;
    }

    public RandomCaveChestRoom getNewCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.basicCaveChest, lootRotation, ChestRoomSet.stone, ChestRoomSet.wood);
        chestRoom.replaceTile(TileRegistry.stoneFloorID, random.getOneOf(TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID));
        return chestRoom;
    }

    public RandomCaveChestRoom getNewDeepCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.deepCaveChest, lootRotation, ChestRoomSet.deepStone, ChestRoomSet.obsidian);
        chestRoom.replaceTile(TileRegistry.deepStoneFloorID, random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
        return chestRoom;
    }

    public CaveRuins getNewCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.stone, WallSet.wood);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("woodfloor", "woodfloor", "stonefloor", "stonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.basicCaveRuinsChest, lootRotation);
    }

    public CaveRuins getNewDeepCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.deepStone, WallSet.obsidian);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("deepstonefloor", "deepstonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.basicDeepCaveRuinsChest, lootRotation);
    }

    public int getBiomeBlendingPriority() {
        return 0;
    }

    public Biome setGenerationWeight(float generationWeight) {
        this.generationWeight = generationWeight;
        return this;
    }

    public float getBiomeGenerationWeight() {
        return this.generationWeight;
    }

    public int getGenerationWaterTileID() {
        return TileRegistry.waterID;
    }

    public int getGenerationCaveLavaTileID() {
        return TileRegistry.lavaID;
    }

    public int getGenerationDeepCaveLavaTileID() {
        return TileRegistry.lavaID;
    }

    public int getGenerationBeachTileID() {
        return TileRegistry.sandID;
    }

    public boolean doesGenerationPreventsBeachTiles() {
        return false;
    }

    public int getGenerationTerrainTileID() {
        return TileRegistry.dirtID;
    }

    public int getGenerationCaveTileID() {
        return TileRegistry.rockID;
    }

    public int getGenerationCaveRockObjectID() {
        return ObjectRegistry.rockID;
    }

    public float getGenerationCaveRockObjectChance() {
        return 0.37f;
    }

    public int getGenerationDeepCaveTileID() {
        return TileRegistry.deepRockID;
    }

    public int getGenerationDeepCaveRockObjectID() {
        return ObjectRegistry.deepRockID;
    }

    public float getGenerationDeepCaveRockObjectChance() {
        return 0.4f;
    }

    public VillageSet[] getVillageSets() {
        return new VillageSet[]{VillageSet.spruce, VillageSet.oak};
    }

    public void initializeGeneratorStack(BiomeGeneratorStack stack) {
    }

    public void generateRegionSurfaceTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
    }

    public void generateRegionCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
    }

    public void generateRegionDeepCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
    }

    public Color getDebugBiomeColor() {
        return null;
    }

    static {
        defaultSurfaceMobs = new MobSpawnTable().add(80, "zombie").add(20, "zombiearcher");
        defaultCaveMobs = new MobSpawnTable().add(80, "zombie").add(20, "zombiearcher").add(10, MobSpawnTable.canSpawnEither(12, 11, 101), "crawlingzombie").add(15, "goblin").add(10, MobSpawnTable.canSpawnEither(6, 9, 101), "vampire").add(3, MobSpawnTable.canSpawnEither(9, 14, 101), "cavemole");
        forestCaveMobs = new MobSpawnTable().include(defaultCaveMobs);
        defaultDeepCaveMobs = new MobSpawnTable().add(100, "skeleton").add(40, "skeletonthrower").add(45, "deepcavespirit");
        commonFish = new FishingLootTable().addFreshWater(100, "carp").startCustom(100).onlySaltWater().onlyBiomes(SnowBiome.class).end("cod").startCustom(100).onlySaltWater().onlyBiomes(ForestBiome.class, PlainsBiome.class).end("herring").startCustom(100).onlySaltWater().onlyBiomes(ForestBiome.class, PlainsBiome.class, SwampBiome.class, DesertBiome.class).end("mackerel").addWater(100, "salmon").startCustom(100).onlyFreshWater().onlyBiomes(ForestBiome.class, PlainsBiome.class, SnowBiome.class).end("trout").startCustom(100).onlySaltWater().onlyBiomes(DesertBiome.class, SwampBiome.class).end("tuna");
        defaultSurfaceFish = new FishingLootTable().addAll(commonFish).addWater(100, "gobfish").addWater(100, "halffish").addWater(5, "fins");
        defaultCaveFish = new FishingLootTable().addAll(commonFish).addWater(100, "rockfish").addWater(50, "terrorfish");
        defaultSurfaceCritters = new MobSpawnTable().add(100, "rabbit").add(80, "squirrel").add(50, "bird").add(20, "bluebird").add(20, "cardinalbird").add(60, "duck");
        defaultCaveCritters = new MobSpawnTable().add(100, "spider").add(100, "mouse").add(5, "beetcavecroppler");
    }
}

