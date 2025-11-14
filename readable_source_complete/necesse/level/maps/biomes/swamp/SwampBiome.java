/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.swamp;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.engine.util.TicketSystemList;
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
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.swamp.SwampCaveLevel;
import necesse.level.maps.biomes.swamp.SwampDeepCaveLevel;
import necesse.level.maps.biomes.swamp.SwampSurfaceLevel;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.VillageSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.regionSystem.Region;

public class SwampBiome
extends Biome {
    public static FishingLootTable swampSurfaceFish = new FishingLootTable(defaultSurfaceFish).addWater(120, "swampfish");
    public static MobSpawnTable surfaceMobs = new MobSpawnTable().include(defaultSurfaceMobs).add(50, "swampzombie").add(50, "swampslime");
    public static MobSpawnTable caveMobs = new MobSpawnTable().add(100, "swampzombie").add(100, "swampslime").add(60, "swampshooter");
    public static MobSpawnTable deepSwampCaveMobs = new MobSpawnTable().add(70, "ancientskeleton").add(25, "ancientskeletonthrower").add(30, "swampskeleton").add(40, "swampdweller").add(70, "giantswampslime").add(40, "mosquitoegg");
    public static MobSpawnTable surfaceCritters = new MobSpawnTable().add(100, "swampslug").add(80, "frog").add(40, "bird").add(40, "cardinalbird").add(40, "duck");
    public static MobSpawnTable caveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "swampstonecaveling").add(100, "frog");
    public static MobSpawnTable deepCaveCritters = new MobSpawnTable().include(Biome.defaultCaveCritters).add(100, "deepswampstonecaveling").add(100, "frog");
    public static LootItemInterface randomSpikedFossilDrop = new LootItemList(new ChanceLootItem(0.005f, "spikedfossil"));
    public static LootItemInterface randomDecayingLeafDrop = new LootItemList(new ChanceLootItem(0.004f, "decayingleaf"));

    @Override
    public SoundSettings getWindSound(Level level) {
        return SoundSettingsRegistry.windSwamp;
    }

    @Override
    public int getRainTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(300, 420);
    }

    @Override
    public int getDryTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(180, 240);
    }

    @Override
    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new SwampSurfaceLevel(islandX, islandY, islandSize, worldEntity, this);
    }

    @Override
    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new SwampCaveLevel(islandX, islandY, dimension, worldEntity, (Biome)this);
    }

    @Override
    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new SwampDeepCaveLevel(islandX, islandY, dimension, worldEntity, (Biome)this);
    }

    @Override
    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        if (!spot.tile.level.isCave) {
            return swampSurfaceFish;
        }
        return super.getFishingLootTable(spot);
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        }
        if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return deepSwampCaveMobs;
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
                return new LootTable(randomSpikedFossilDrop, super.getExtraMobDrops(mob));
            }
            if (mob.getLevel().getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new LootTable(randomDecayingLeafDrop, super.getExtraMobDrops(mob));
            }
        }
        return super.getExtraMobDrops(mob);
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            if (level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                return new MusicList(MusicRegistry.SwampCavern);
            }
            return new MusicList(MusicRegistry.MurkyMire);
        }
        if (level.getWorldEntity().isNight()) {
            return new MusicList(MusicRegistry.GatorsLullaby);
        }
        return new MusicList(MusicRegistry.WatersideSerenade);
    }

    @Override
    public LootTable getExtraBiomeMobDrops(LevelIdentifier levelIdentifier) {
        if (levelIdentifier == null) {
            return new LootTable();
        }
        if (levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            return new LootTable(randomSpikedFossilDrop);
        }
        if (levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return new LootTable(randomDecayingLeafDrop);
        }
        return new LootTable();
    }

    @Override
    public GameTile getUnderLiquidTile(Level level, int tileX, int tileY) {
        if (level.isCave) {
            return TileRegistry.getTile(TileRegistry.dirtID);
        }
        return TileRegistry.getTile(TileRegistry.swampGrassID);
    }

    @Override
    public int getBiomeBlendingPriority() {
        return 200;
    }

    @Override
    public RandomCaveChestRoom getNewCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.swampCaveChest, lootRotation, ChestRoomSet.swampStone, ChestRoomSet.wood);
        chestRoom.replaceTile(TileRegistry.stoneFloorID, random.getOneOf(TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID));
        chestRoom.replaceTile(TileRegistry.swampStoneFloorID, random.getOneOf(TileRegistry.swampStoneFloorID, TileRegistry.swampStoneBrickFloorID));
        return chestRoom;
    }

    @Override
    public RandomCaveChestRoom getNewDeepCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(random, LootTablePresets.deepSwampCaveChest, lootRotation, ChestRoomSet.deepSwampStone, ChestRoomSet.deepStone);
        chestRoom.replaceTile(TileRegistry.deepStoneFloorID, random.getOneOf(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID));
        chestRoom.replaceTile(TileRegistry.deepSwampStoneFloorID, random.getOneOf(TileRegistry.deepSwampStoneFloorID, TileRegistry.deepSwampStoneBrickFloorID));
        return chestRoom;
    }

    @Override
    public CaveRuins getNewCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.swampStone, WallSet.wood);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("woodfloor", "woodfloor", "swampstonefloor", "swampstonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.swampCaveRuinsChest, lootRotation);
    }

    @Override
    public CaveRuins getNewDeepCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        WallSet wallSet = random.getOneOf(WallSet.deepSwampStone, WallSet.deepStone);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.oak, FurnitureSet.spruce);
        String floorStringID = random.getOneOf("deepswampstonefloor", "deepswampstonebrickfloor");
        return random.getOneOf(CaveRuins.caveRuinGetters).get(random, wallSet, furnitureSet, floorStringID, LootTablePresets.swampDeepCaveRuinsChest, lootRotation);
    }

    @Override
    public boolean doesGenerationPreventsBeachTiles() {
        return true;
    }

    @Override
    public int getGenerationTerrainTileID() {
        return TileRegistry.swampGrassID;
    }

    @Override
    public int getGenerationCaveTileID() {
        return TileRegistry.swampRockID;
    }

    @Override
    public int getGenerationCaveRockObjectID() {
        return ObjectRegistry.swampRockID;
    }

    @Override
    public int getGenerationDeepCaveTileID() {
        return TileRegistry.deepSwampRockID;
    }

    @Override
    public int getGenerationDeepCaveRockObjectID() {
        return ObjectRegistry.deepSwampRockID;
    }

    @Override
    public VillageSet[] getVillageSets() {
        return null;
    }

    @Override
    public void initializeGeneratorStack(BiomeGeneratorStack stack) {
        super.initializeGeneratorStack(stack);
        stack.addRandomSimplexVeinsBranch("swampOvergrownGrass", 1.5f, 0.4f, 0.7f, 2);
        stack.addRandomSimplexVeinsBranch("swampMudPatches", 2.0f, 0.5f, 0.7f, 2);
        stack.addRandomSimplexVeinsBranch("swampWillowTrees", 2.0f, 0.2f, 1.0f, 0);
        stack.addRandomVeinsBranch("swampPurpleFlowerPatch", 0.05f, 5, 15, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampCows", 0.015f, 8, 12, 0.1f, 0, false);
        stack.addRandomVeinsBranch("swampSheep", 0.015f, 8, 12, 0.1f, 0, false);
        stack.addRandomSimplexVeinsBranch("swampWaterGrass", 2.0f, 0.33f, 1.0f, 0);
        stack.addRandomSimplexVeinsBranch("swampCaveWaterGrass", 2.0f, 0.33f, 1.0f, 0);
        stack.addRandomVeinsBranch("swampIvyOre", 0.48f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampCopper", 0.48f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampIron", 0.4f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampGold", 0.24f, 3, 6, 0.4f, 2, false);
        stack.addRandomSimplexVeinsBranch("swampDeepTallGrass", 2.0f, 0.4f, 1.0f, 0);
        stack.addRandomVeinsBranch("swampDeepCopper", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampDeepIron", 0.16f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampDeepGold", 0.24f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampDeepTungsten", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampDeepLifeQuartz", 0.08f, 3, 6, 0.4f, 2, false);
        stack.addRandomVeinsBranch("swampDeepMycelium", 0.27f, 3, 6, 0.4f, 2, false);
    }

    @Override
    public void generateRegionSurfaceTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionSurfaceTerrain(region, stack, random);
        int grassTile = TileRegistry.swampGrassID;
        stack.startPlaceOnVein(this, region, random, "swampOvergrownGrass").onlyOnTile(grassTile).chance(0.7f).placeTile(TileRegistry.overgrownSwampGrassID);
        final GameObject wildMushroom = ObjectRegistry.getObject("wildmushroom");
        stack.startPlaceOnVein(this, region, random, "swampMudPatches").onlyOnTile(grassTile).customPlace(new GeneratorPlaceFactory.RegionPlaceFunction(){

            @Override
            public void place(GameRandom random, Region region, int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                if (random.getChance(0.7f)) {
                    region.tileLayer.setTileByRegion(regionTileX, regionTileY, TileRegistry.mudID);
                }
                if (region.objectLayer.getObjectIDByRegion(ObjectLayerRegistry.BASE_LAYER, regionTileX, regionTileY) == 0 && random.getChance(0.15f) && wildMushroom.canPlace(level, tileX, tileY, 0, false) == null) {
                    wildMushroom.placeObject(level, tileX, tileY, 0, false);
                }
            }
        });
        stack.startPlaceOnVein(this, region, random, "swampWillowTrees").onlyOnTile(grassTile).chance(0.08f).placeObject("willowtree");
        stack.startPlace(this, region, random).chance(0.5).testTileID(tileID -> tileID == grassTile || tileID == TileRegistry.overgrownSwampGrassID).placeObject("swampgrass");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(0.005f).placeObject("purpleflowerpatch");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(0.001f).placeObjectRandomRotation("willowtreestump");
        stack.startPlace(this, region, random).chance(0.0015f).placeObject("swampsurfacerock");
        stack.startPlace(this, region, random).chance(0.0025f).placeObject("swampsurfacerocksmall");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(0.008f).placeObject("swamproot");
        stack.startPlaceOnVein(this, region, random, "swampPurpleFlowerPatch").onlyOnTile(grassTile).chance(0.4f).placeObject("purpleflowerpatch");
        ProtectedTicketSystemList cowSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "cow")).addObject(25, "bull");
        stack.startPlaceOnVein(this, region, random, "swampCows").onlyOnTile(grassTile).placeMob((TicketSystemList<String>)cowSpawns);
        ProtectedTicketSystemList sheepSpawns = ((TicketSystemList)new TicketSystemList().addObject(100, "sheep")).addObject(25, "ram");
        stack.startPlaceOnVein(this, region, random, "swampSheep").onlyOnTile(grassTile).placeMob((TicketSystemList<String>)sheepSpawns);
        region.updateLiquidManager();
        final GameObject cattail = ObjectRegistry.getObject("cattail");
        final GameObject reeds = ObjectRegistry.getObject("reeds");
        stack.startPlaceOnVein(this, region, random, "swampWaterGrass").customPlace(new GeneratorPlaceFactory.RegionPlaceFunction(){

            @Override
            public void place(GameRandom random, Region region, int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                if (random.getChance(0.2f) && reeds.canPlace(level, tileX, tileY, 0, false) == null) {
                    reeds.placeObject(level, tileX, tileY, 0, false);
                }
                if (random.getChance(0.2f) && cattail.canPlace(level, tileX, tileY, 0, false) == null) {
                    cattail.placeObject(level, tileX, tileY, 0, false);
                }
            }
        });
    }

    @Override
    public void generateRegionCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("swampcaverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("swampcaverocksmall");
        final GameObject cattail = ObjectRegistry.getObject("cattail");
        final GameObject reeds = ObjectRegistry.getObject("reeds");
        stack.startPlaceOnVein(this, region, random, "swampCaveWaterGrass").customPlace(new GeneratorPlaceFactory.RegionPlaceFunction(){

            @Override
            public void place(GameRandom random, Region region, int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                if (random.getChance(0.2f) && reeds.canPlace(level, tileX, tileY, 0, false) == null) {
                    reeds.placeObject(level, tileX, tileY, 0, false);
                }
                if (random.getChance(0.2f) && cattail.canPlace(level, tileX, tileY, 0, false) == null) {
                    cattail.placeObject(level, tileX, tileY, 0, false);
                }
            }
        });
        stack.startPlace(this, region, random).onlyOnTile(TileRegistry.swampRockID).onlyOnObject(0).chance(0.6f).placeObjectForced("swampgrass");
        stack.startPlace(this, region, random).chance(0.03f).placeCrates("swampcrate");
        stack.startPlaceOnVein(this, region, random, "swampIvyOre").onlyOnObject(ObjectRegistry.swampRockID).placeObjectForced("ivyoreswamp");
        stack.startPlaceOnVein(this, region, random, "swampCopper").onlyOnObject(ObjectRegistry.swampRockID).placeObjectForced("copperoreswamp");
        stack.startPlaceOnVein(this, region, random, "swampIron").onlyOnObject(ObjectRegistry.swampRockID).placeObjectForced("ironoreswamp");
        stack.startPlaceOnVein(this, region, random, "swampGold").onlyOnObject(ObjectRegistry.swampRockID).placeObjectForced("goldoreswamp");
    }

    @Override
    public void generateRegionDeepCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionDeepCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.005f).placeObject("deepswampcaverock");
        stack.startPlace(this, region, random).chance(0.01f).placeObject("deepswampcaverocksmall");
        stack.startPlaceOnVein(this, region, random, "swampDeepTallGrass").onlyOnTile(TileRegistry.deepSwampRockID).chance(0.85f).placeObject("deepswamptallgrass");
        stack.startPlace(this, region, random).onlyOnTile(TileRegistry.deepSwampRockID).onlyOnObject(0).chance(0.6f).placeObjectForced("deepswampgrass");
        stack.startPlace(this, region, random).chance(0.03f).placeCrates("swampcrate");
        stack.startPlaceOnVein(this, region, random, "swampIvyOre").onlyOnObject(ObjectRegistry.deepSwampRockID).placeObjectForced("copperoredeepswamprock");
        stack.startPlaceOnVein(this, region, random, "swampCopper").onlyOnObject(ObjectRegistry.deepSwampRockID).placeObjectForced("ironoredeepswamprock");
        stack.startPlaceOnVein(this, region, random, "swampIron").onlyOnObject(ObjectRegistry.deepSwampRockID).placeObjectForced("goldoredeepswamprock");
        stack.startPlaceOnVein(this, region, random, "swampGold").onlyOnObject(ObjectRegistry.deepSwampRockID).placeObjectForced("tungstenoredeepswamprock");
        stack.startPlaceOnVein(this, region, random, "swampGold").onlyOnObject(ObjectRegistry.deepSwampRockID).placeObjectForced("lifequartzdeepswamprock");
        stack.startPlaceOnVein(this, region, random, "swampGold").onlyOnObject(ObjectRegistry.deepSwampRockID).placeObjectForced("myceliumoredeepswamprock");
    }

    @Override
    public Color getDebugBiomeColor() {
        return new Color(78, 97, 50);
    }
}

