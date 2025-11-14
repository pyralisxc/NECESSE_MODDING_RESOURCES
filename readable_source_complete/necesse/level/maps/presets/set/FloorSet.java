/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class FloorSet
extends PresetSet<FloorSet> {
    public static final FloorSet wood = (FloorSet)((FloorSet)((FloorSet)((FloorSet)new FloorSet(TileRegistry.woodFloorID).surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST)).deepCave(BiomeRegistry.FOREST)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION);
    public static final FloorSet pine = (FloorSet)((FloorSet)((FloorSet)new FloorSet(TileRegistry.pineFloorID).surface(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).cave(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
    public static final FloorSet palm = (FloorSet)((FloorSet)((FloorSet)((FloorSet)new FloorSet(TileRegistry.palmFloorID).surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT)).incursion(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION);
    public static final FloorSet willow = (FloorSet)((FloorSet)((FloorSet)((FloorSet)new FloorSet(TileRegistry.willowFloorID).surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SWAMP)).incursion();
    public static final FloorSet dryad = (FloorSet)((FloorSet)((FloorSet)new FloorSet(TileRegistry.dryadFloorID).surface(BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.PLAINS)).incursion();
    public static final FloorSet stone = (FloorSet)((FloorSet)new FloorSet(TileRegistry.stoneFloorID).surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST);
    public static final FloorSet stoneBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.stoneBrickFloorID).surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST);
    public static final FloorSet stoneTiled = (FloorSet)((FloorSet)new FloorSet(TileRegistry.stoneTiledFloorID).surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST);
    public static final FloorSet snowStone = (FloorSet)((FloorSet)((FloorSet)new FloorSet(TileRegistry.snowStoneFloorID).surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).incursion();
    public static final FloorSet snowRock = new FloorSet(TileRegistry.snowRockID);
    public static final FloorSet snowStoneBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.snowStoneBrickFloorID).surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW);
    public static final FloorSet swampStone = (FloorSet)((FloorSet)new FloorSet(TileRegistry.swampStoneFloorID).surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP);
    public static final FloorSet swampStoneBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.swampStoneBrickFloorID).surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP);
    public static final FloorSet sandstone = (FloorSet)((FloorSet)new FloorSet(TileRegistry.sandstoneFloorID).surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT);
    public static final FloorSet sandBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.sandBrickID).surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT);
    public static final FloorSet sandstoneBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.sandstoneBrickFloorID).surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT);
    public static final FloorSet granite = (FloorSet)((FloorSet)new FloorSet(TileRegistry.graniteFloorID).surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS);
    public static final FloorSet graniteBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.graniteBrickFloorID).surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS);
    public static final FloorSet deepStone = (FloorSet)((FloorSet)new FloorSet(TileRegistry.deepStoneFloorID).deepCave(BiomeRegistry.FOREST)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION);
    public static final FloorSet deepStoneBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.deepStoneBrickFloorID).deepCave(BiomeRegistry.FOREST)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION);
    public static final FloorSet deepStoneTiled = (FloorSet)((FloorSet)new FloorSet(TileRegistry.deepStoneTiledFloorID).deepCave(BiomeRegistry.FOREST)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION);
    public static final FloorSet deepSnowStone = (FloorSet)((FloorSet)new FloorSet(TileRegistry.deepSnowStoneFloorID).deepCave(BiomeRegistry.SNOW)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
    public static final FloorSet deepSnowStoneBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.deepSnowStoneBrickFloorID).deepCave(BiomeRegistry.SNOW)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
    public static final FloorSet deepSwampStone = (FloorSet)((FloorSet)new FloorSet(TileRegistry.deepSwampStoneFloorID).deepCave(BiomeRegistry.SWAMP)).incursion(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION);
    public static final FloorSet deepSwampStoneBrick = (FloorSet)((FloorSet)new FloorSet(TileRegistry.deepSwampStoneBrickFloorID).deepCave(BiomeRegistry.SWAMP)).incursion(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION);
    public static final FloorSet basalt = (FloorSet)((FloorSet)new FloorSet(TileRegistry.basaltFloorID).deepCave(BiomeRegistry.PLAINS)).incursion();
    public static final FloorSet dungeon = new FloorSet(TileRegistry.dungeonFloorID);
    public static final FloorSet bamboo = (FloorSet)((FloorSet)new FloorSet(TileRegistry.bambooFloorID).deepCave(BiomeRegistry.SWAMP)).incursion();
    public static final FloorSet deadWood = (FloorSet)new FloorSet(TileRegistry.deadWoodFloorID).incursion();
    public static final FloorSet spiderCastle = (FloorSet)new FloorSet(TileRegistry.getTileID("spidercastlefloor")).incursion(BiomeRegistry.SPIDER_CASTLE);
    public final int tile;

    protected FloorSet(int tile) {
        this.tile = tile;
        this.tileArrays = new int[][]{{this.tile}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(FloorSet.class);
    }
}

