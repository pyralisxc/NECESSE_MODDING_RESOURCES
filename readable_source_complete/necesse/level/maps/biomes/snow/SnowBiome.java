/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.snow;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.AbstractMusicList;
import necesse.engine.GameTileRange;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.FrozenMobImmuneBuff;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.snow.SnowCaveLevel;
import necesse.level.maps.biomes.snow.SnowDeepCaveLevel;
import necesse.level.maps.biomes.snow.SnowSurfaceLevel;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.VillageSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.regionSystem.Region;

public class SnowBiome
extends Biome {
    public static FishingLootTable snowSurfaceFish = new FishingLootTable(defaultSurfaceFish).addWater(120, "icefish");
    public static MobSpawnTable surfaceMobs = new MobSpawnTable().include(defaultSurfaceMobs).add(50, "trapperzombie");
    public static GameTileRange frozenMobRange = new GameTileRange(6, new Point[0]);
    public static TicketSystemList<String> frozenMobStringIDs = new TicketSystemList();
    public static MobSpawnTable caveMobs;
    public static MobSpawnTable deepSnowCaveMobs;
    public static MobSpawnTable surfaceCritters;
    public static MobSpawnTable caveCritters;
    public static MobSpawnTable deepCaveCritters;
    public static LootItemInterface randomRoyalEggDrop;
    public static LootItemInterface randomIceCrownDrop;

    public static ArrayList<Mob> getSpawnedFrozenMobs(Level level, ServerClient client, int tileX, int tileY, float spawnChance, Predicate<Mob> beforeTest) {
        ArrayList<Mob> spawnedMobs = new ArrayList<Mob>();
        int spawnRoomID = level.regionManager.getRoomIDByTile(tileX, tileY);
        for (Point validTile : frozenMobRange.getValidTiles(tileX, tileY)) {
            Mob mob;
            if (!GameRandom.globalRandom.getChance(spawnChance) || level.isSolidTile(validTile.x, validTile.y) || spawnRoomID != level.regionManager.getRoomIDByTile(validTile.x, validTile.y) || !FrozenMobImmuneBuff.isBuffValidForTarget(mob = MobRegistry.getMob(frozenMobStringIDs.getRandomObject(GameRandom.globalRandom), level)) || beforeTest != null && !beforeTest.test(mob)) continue;
            Point moveOffset = mob.getPathMoveOffset();
            if (!mob.isValidSpawnLocation(level.getServer(), client, validTile.x * 32 + moveOffset.x, validTile.y * 32 + moveOffset.y)) continue;
            mob.onSpawned(validTile.x * 32 + moveOffset.x, validTile.y * 32 + moveOffset.y);
            mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FROZEN_MOB_IMMUNE, mob, 2400.0f, null), false);
            spawnedMobs.add(mob);
        }
        return spawnedMobs;
    }

    @Override
    public SoundSettings getWindSound(Level level) {
        return SoundSettingsRegistry.windSnow;
    }

    @Override
    protected void loadRainTexture() {
        this.rainTexture = rainTextures.addTexture(GameTexture.fromFile("snowfall"));
    }

    @Override
    public Color getRainColor(Level level, int tileX, int tileY) {
        return new Color(255, 255, 255, 200);
    }

    @Override
    public void tickRainEffect(GameCamera camera, Level level, int tileX, int tileY, float rainAlpha) {
    }

    @Override
    public GameSound getRainSound(Level level) {
        return null;
    }

    @Override
    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new SnowSurfaceLevel(islandX, islandY, islandSize, worldEntity, this);
    }

    @Override
    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new SnowCaveLevel(islandX, islandY, dimension, worldEntity, (Biome)this);
    }

    @Override
    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new SnowDeepCaveLevel(islandX, islandY, dimension, worldEntity, (Biome)this);
    }

    @Override
    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        if (!spot.tile.level.isCave) {
            return snowSurfaceFish;
        }
        return super.getFishingLootTable(spot);
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        }
        if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return deepSnowCaveMobs;
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
                return new LootTable(randomRoyalEggDrop, super.getExtraMobDrops(mob));
            }
            if (mob.getLevel().getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new LootTable(randomIceCrownDrop, super.getExtraMobDrops(mob));
            }
        }
        return super.getExtraMobDrops(mob);
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new MusicList(MusicRegistry.SubzeroSanctum);
            }
            return new MusicList(MusicRegistry.GlaciersEmbrace);
        }
        if (level.getWorldEntity().isNight()) {
            return new MusicList(MusicRegistry.PolarNight);
        }
        return new MusicList(MusicRegistry.AuroraTundra);
    }

    @Override
    public LootTable getExtraBiomeMobDrops(LevelIdentifier levelIdentifier) {
        if (levelIdentifier == null) {
            return new LootTable();
        }
        if (levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            return new LootTable(randomRoyalEggDrop);
        }
        if (levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return new LootTable(randomIceCrownDrop);
        }
        return new LootTable();
    }

    @Override
    public GameTile getUnderLiquidTile(Level level, int tileX, int tileY) {
        return TileRegistry.getTile(TileRegistry.snowID);
    }

    @Override
    public int getBiomeBlendingPriority() {
        return 100;
    }

    @Override
    public RandomCaveChestRoom getNewCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.snowCaveChest, lootRotation, ChestRoomSet.snowStone, ChestRoomSet.ice, ChestRoomSet.wood);
        chestRoom.replaceTile(TileRegistry.stoneFloorID, random.getOneOf(TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID));
        chestRoom.replaceTile(TileRegistry.snowStoneFloorID, random.getOneOf(TileRegistry.snowStoneFloorID, TileRegistry.snowStoneBrickFloorID));
        return chestRoom;
    }

    @Override
    public RandomCaveChestRoom getNewDeepCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.deepSnowCaveChest, lootRotation, ChestRoomSet.deepStone, ChestRoomSet.deepSnowStone);
        chestRoom.replaceTile(TileRegistry.deepStoneFloorID, random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
        chestRoom.replaceTile(TileRegistry.deepSnowStoneFloorID, random.getOneOf(TileRegistry.deepSnowStoneFloorID, TileRegistry.deepSnowStoneBrickFloorID));
        return chestRoom;
    }

    @Override
    public CaveRuins getNewCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.snowStone, WallSet.wood);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.pine, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("woodfloor", "woodfloor", "snowstonefloor", "snowstonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.snowCaveRuinsChest, lootRotation);
    }

    @Override
    public CaveRuins getNewDeepCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.deepStone, WallSet.deepSnowStone);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.pine, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("deepsnowstonefloor", "deepsnowstonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.snowDeepCaveRuinsChest, lootRotation);
    }

    @Override
    public int getGenerationCaveLavaTileID() {
        return TileRegistry.iceID;
    }

    @Override
    public int getGenerationDeepCaveLavaTileID() {
        return TileRegistry.deepIceID;
    }

    @Override
    public int getGenerationBeachTileID() {
        return TileRegistry.iceID;
    }

    @Override
    public int getGenerationTerrainTileID() {
        return TileRegistry.snowID;
    }

    @Override
    public int getGenerationCaveTileID() {
        return TileRegistry.snowRockID;
    }

    @Override
    public int getGenerationCaveRockObjectID() {
        return ObjectRegistry.snowRockID;
    }

    @Override
    public int getGenerationDeepCaveTileID() {
        return TileRegistry.deepSnowRockID;
    }

    @Override
    public int getGenerationDeepCaveRockObjectID() {
        return ObjectRegistry.deepSnowRockID;
    }

    @Override
    public VillageSet[] getVillageSets() {
        return new VillageSet[]{VillageSet.pine};
    }

    @Override
    public void initializeGeneratorStack(BiomeGeneratorStack stack) {
        super.initializeGeneratorStack(stack);
        stack.addRandomSimplexVeinsBranch("snowPineTrees", 2.0f, 0.2f, 1.0f, 0);
        stack.addRandomVeinsBranch("snowBlueFlowerPatch", 0.05f, 5, 15, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowIceBlossomPatch", 0.05f, 6, 12, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowBlackberries", 0.065f, 8, 10, 0.1f, 0, false);
        stack.addRandomVeinsBranch("snowSheep", 0.02f, 8, 12, 0.1f, 0, false);
        stack.addRandomVeinsBranch("snowPenguins", 0.02f, 8, 12, 0.1f, 0, false);
        stack.addRandomVeinsBranch("snowFrostShards", 0.48f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowCopper", 0.48f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowIron", 0.4f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowGold", 0.24f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowFallingIcicles", 0.8f, 7, 20, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowDeepCopper", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowDeepIron", 0.4f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowDeepGold", 0.24f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowDeepTungsten", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowDeepLifeQuartz", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("snowDeepGlacial", 0.27f, 3, 6, 0.4f, 2, false);
    }

    @Override
    public void generateRegionSurfaceTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionSurfaceTerrain(region, stack, random);
        int snowTile = TileRegistry.snowID;
        stack.startPlaceOnVein(this, region, random, "snowPineTrees").onlyOnTile(snowTile).chance(0.08f).placeObject("pinetree");
        for (int i = 0; i < 4; ++i) {
            stack.startPlace(this, region, random).chance(0.05f).onlyOnTile(snowTile).placeObject("snowpile" + i);
        }
        stack.startPlace(this, region, random).onlyOnTile(snowTile).chance(0.005f).placeObject("blueflowerpatch");
        stack.startPlace(this, region, random).chance(0.0015f).placeObject("snowsurfacerock");
        stack.startPlace(this, region, random).chance(0.0025f).placeObject("snowsurfacerocksmall");
        stack.startPlaceOnVein(this, region, random, "snowBlackberries").onlyOnTile(snowTile).placeObjectFruitGrower("blackberrybush");
        stack.startPlaceOnVein(this, region, random, "snowBlueFlowerPatch").onlyOnTile(snowTile).chance(0.4f).placeObject("blueflowerpatch");
        stack.startPlaceOnVein(this, region, random, "snowIceBlossomPatch").onlyOnTile(snowTile).chance(0.2f).placeObject("wildiceblossom");
        ProtectedTicketSystemList sheepSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "sheep")).addObject(25, "ram");
        stack.startPlaceOnVein(this, region, random, "snowSheep").onlyOnTile(snowTile).placeMob((TicketSystemList<String>)sheepSpawns);
        stack.startPlaceOnVein(this, region, random, "snowPenguins").onlyOnTile(snowTile).placeMob("penguin");
        stack.startPlace(this, region, random).onlyOnTile(snowTile).chancePerRegion(0.02f).placeMob("polarbear");
    }

    @Override
    public void generateRegionCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("snowcaverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("snowcaverocksmall");
        stack.startPlace(this, region, random).chance(0.03f).placeCrates("snowcrate");
        stack.startPlaceOnVein(this, region, random, "snowFrostShards").onlyOnObject(ObjectRegistry.snowRockID).placeObjectForced("frostshardsnow");
        stack.startPlaceOnVein(this, region, random, "snowCopper").onlyOnObject(ObjectRegistry.snowRockID).placeObjectForced("copperoresnow");
        stack.startPlaceOnVein(this, region, random, "snowIron").onlyOnObject(ObjectRegistry.snowRockID).placeObjectForced("ironoresnow");
        stack.startPlaceOnVein(this, region, random, "snowGold").onlyOnObject(ObjectRegistry.snowRockID).placeObjectForced("goldoresnow");
    }

    @Override
    public void generateRegionDeepCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionDeepCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("deepsnowcaverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("deepsnowcaverocksmall");
        stack.startPlace(this, region, random).chance(0.03f).placeCrates("snowcrate");
        stack.startPlaceOnVein(this, region, random, "snowFallingIcicles").onlyOnObject(0).chance(0.4f).placeObject("fallingicicletrigger");
        stack.startPlaceOnVein(this, region, random, "snowDeepCopper").onlyOnObject(ObjectRegistry.deepSnowRockID).placeObjectForced("copperoredeepsnowrock");
        stack.startPlaceOnVein(this, region, random, "snowDeepIron").onlyOnObject(ObjectRegistry.deepSnowRockID).placeObjectForced("ironoredeepsnowrock");
        stack.startPlaceOnVein(this, region, random, "snowDeepGold").onlyOnObject(ObjectRegistry.deepSnowRockID).placeObjectForced("goldoredeepsnowrock");
        stack.startPlaceOnVein(this, region, random, "snowDeepTungsten").onlyOnObject(ObjectRegistry.deepSnowRockID).placeObjectForced("tungstenoredeepsnowrock");
        stack.startPlaceOnVein(this, region, random, "snowDeepLifeQuartz").onlyOnObject(ObjectRegistry.deepSnowRockID).placeObjectForced("lifequartzdeepsnowrock");
        stack.startPlaceOnVein(this, region, random, "snowDeepGlacial").onlyOnObject(ObjectRegistry.deepSnowRockID).placeObjectForced("glacialoredeepsnowrock");
    }

    @Override
    public Color getDebugBiomeColor() {
        return new Color(223, 244, 255);
    }

    static {
        frozenMobStringIDs.addObject(100, (Object)"zombie");
        frozenMobStringIDs.addObject(75, (Object)"zombiearcher");
        frozenMobStringIDs.addObject(100, (Object)"trapperzombie");
        frozenMobStringIDs.addObject(75, (Object)"frozendwarf");
        caveMobs = new MobSpawnTable().add(60, "zombie").add(20, "zombiearcher").add(30, "trapperzombie").add(30, "crawlingzombie").add(40, "frozendwarf").add(15, "frostsentry").add(15, "vampire").add(3, "cavemole").add(new MobChance(30){

            @Override
            public boolean canSpawn(Level level, ServerClient client, Point spawnTile, String purpose) {
                if (!purpose.equals("mobspawning")) {
                    return false;
                }
                int tileRange = 20;
                return level.entityManager.mobs.streamInRegionsInTileRange(spawnTile.x * 32 + 16, spawnTile.y * 32 + 16, tileRange).noneMatch(m -> m.buffManager.hasBuff(BuffRegistry.FROZEN_MOB_IMMUNE) && GameMath.diagonalMoveDistance(m.getX(), m.getY(), spawnTile.x * 32 + 16, spawnTile.y * 32 + 16) < (double)(tileRange * 32));
            }

            @Override
            public Mob getMob(Level level, ServerClient client, Point spawnTile) {
                return null;
            }

            @Override
            public Collection<Mob> spawnMob(Level level, ServerClient client, Point spawnTile, Predicate<Mob> beforeTest, Consumer<Mob> beforeAdded, String purpose) {
                ArrayList<Mob> spawnedMobs = SnowBiome.getSpawnedFrozenMobs(level, client, spawnTile.x, spawnTile.y, 0.3f, beforeTest);
                if (spawnedMobs.isEmpty()) {
                    return null;
                }
                while (spawnedMobs.size() > 15) {
                    spawnedMobs.remove(GameRandom.globalRandom.nextInt(spawnedMobs.size()));
                }
                for (Mob mob : spawnedMobs) {
                    if (beforeAdded != null) {
                        beforeAdded.accept(mob);
                    }
                    level.entityManager.mobs.add(mob);
                }
                return spawnedMobs;
            }
        });
        deepSnowCaveMobs = new MobSpawnTable().add(120, "snowwolf").add(70, "skeleton").add(25, "skeletonthrower").add(50, "cryoflake").add(15, "ninja");
        surfaceCritters = new MobSpawnTable().add(100, "snowhare").add(60, "bluebird").add(20, "bird").add(60, "duck");
        caveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "snowstonecaveling");
        deepCaveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "deepsnowstonecaveling");
        randomRoyalEggDrop = new LootItemList(new ChanceLootItem(0.005f, "royalegg"));
        randomIceCrownDrop = new LootItemList(new ChanceLootItem(0.004f, "icecrown"));
    }
}

