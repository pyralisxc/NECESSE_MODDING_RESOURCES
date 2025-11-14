/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class WallSet
extends PresetSet<WallSet> {
    public static final WallSet wood = (WallSet)((WallSet)((WallSet)new WallSet("wood").surface(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).cave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT);
    public static final WallSet pine = (WallSet)((WallSet)((WallSet)((WallSet)new WallSet("pine").surface(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.PLAINS)).cave(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.PLAINS)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
    public static final WallSet willow;
    public static final WallSet palm;
    public static final WallSet bamboo;
    public static final WallSet dryad;
    public static final WallSet stone;
    public static final WallSet sandstone;
    public static final WallSet swampStone;
    public static final WallSet granite;
    public static final WallSet snowStone;
    public static final WallSet ice;
    public static final WallSet brick;
    public static final WallSet dungeon;
    public static final WallSet deepStone;
    public static final WallSet obsidian;
    public static final WallSet deepSnowStone;
    public static final WallSet deepSwampStone;
    public static final WallSet deepSandstone;
    public static final WallSet basalt;
    public static final WallSet crypt;
    public static final WallSet spidercastle;
    public static final WallSet dawn;
    public static final WallSet dusk;
    public static final WallSet ancientruin;
    public final int wall;
    public final int doorClosed;
    public final int doorOpen;
    public final int flameTrap;
    public final int arrowTrap;
    public final int sawTrap;
    public final int window;

    public WallSet(String prefixStringID) {
        this.wall = ObjectRegistry.getObjectID(prefixStringID + "wall");
        this.objectArrays = new int[][]{{this.wall}, {this.doorClosed = ObjectRegistry.getObjectID(prefixStringID + "door")}, {this.arrowTrap = ObjectRegistry.getObjectID(prefixStringID + "arrowtrap")}, {this.flameTrap = ObjectRegistry.getObjectID(prefixStringID + "flametrap")}, {this.sawTrap = ObjectRegistry.getObjectID(prefixStringID + "sawtrap")}, {this.doorOpen = ObjectRegistry.getObjectID(prefixStringID + "dooropen")}, {this.window = ObjectRegistry.getObjectID(prefixStringID + "window")}};
    }

    static {
        palm = (WallSet)((WallSet)((WallSet)((WallSet)new WallSet("palm").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT)).incursion(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION);
        willow = (WallSet)((WallSet)((WallSet)((WallSet)new WallSet("willow").surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SWAMP, BiomeRegistry.FOREST)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.PLAINS, BiomeRegistry.SLIME_CAVE, BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION);
        dryad = (WallSet)((WallSet)((WallSet)new WallSet("dryad").cave(BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.PLAINS)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
        bamboo = (WallSet)((WallSet)((WallSet)new WallSet("bamboo").cave(BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SWAMP)).incursion(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, BiomeRegistry.DESERT_DEEP_CAVE_INCURSION, BiomeRegistry.SLIME_CAVE);
        stone = (WallSet)((WallSet)new WallSet("stone").surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST);
        sandstone = (WallSet)((WallSet)((WallSet)new WallSet("sandstone").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).incursion(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION);
        swampStone = (WallSet)((WallSet)((WallSet)new WallSet("swampstone").surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP)).incursion(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, BiomeRegistry.SLIME_CAVE);
        snowStone = (WallSet)((WallSet)((WallSet)new WallSet("snowstone").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
        granite = (WallSet)((WallSet)((WallSet)new WallSet("granite").surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION);
        ice = (WallSet)((WallSet)((WallSet)new WallSet("ice").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).deepCave(BiomeRegistry.SNOW);
        brick = (WallSet)((WallSet)((WallSet)new WallSet("brick").surface(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).cave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT);
        dungeon = (WallSet)((WallSet)((WallSet)((WallSet)new WallSet("dungeon").surface(BiomeRegistry.SNOW, BiomeRegistry.SWAMP)).cave(BiomeRegistry.SNOW, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SNOW, BiomeRegistry.SWAMP)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
        deepStone = (WallSet)((WallSet)new WallSet("deepstone").deepCave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).incursion(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION, BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION);
        obsidian = (WallSet)((WallSet)new WallSet("obsidian").deepCave(BiomeRegistry.FOREST, BiomeRegistry.SWAMP, BiomeRegistry.PLAINS)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
        deepSnowStone = (WallSet)((WallSet)new WallSet("deepsnowstone").deepCave(BiomeRegistry.SNOW)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
        deepSwampStone = (WallSet)((WallSet)new WallSet("deepswampstone").deepCave(BiomeRegistry.SWAMP)).incursion(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, BiomeRegistry.SLIME_CAVE);
        deepSandstone = (WallSet)((WallSet)((WallSet)new WallSet("deepsandstone").cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT)).incursion(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION);
        basalt = (WallSet)((WallSet)new WallSet("basalt").deepCave(BiomeRegistry.PLAINS)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
        crypt = (WallSet)new WallSet("crypt").incursion(BiomeRegistry.GRAVEYARD);
        spidercastle = (WallSet)new WallSet("spidercastle").incursion(BiomeRegistry.SPIDER_CASTLE);
        dawn = new WallSet("dawn");
        dusk = new WallSet("dusk");
        ancientruin = (WallSet)new WallSet("ancientruin").incursion(BiomeRegistry.CRYSTAL_HOLLOW);
        PresetDebugPreviewForm.registerPresetSet(WallSet.class);
    }
}

