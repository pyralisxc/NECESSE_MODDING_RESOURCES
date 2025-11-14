/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class FurnitureSet
extends PresetSet<FurnitureSet> {
    public static final FurnitureSet oak = (FurnitureSet)((FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("oak").surface(BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.FOREST)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION);
    public static final FurnitureSet spruce = (FurnitureSet)((FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("spruce").surface(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.DESERT)).cave(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.DESERT)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
    public static final FurnitureSet maple = (FurnitureSet)((FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("maple").surface(BiomeRegistry.PLAINS, BiomeRegistry.DESERT)).cave(BiomeRegistry.PLAINS, BiomeRegistry.DESERT)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.DESERT)).incursion();
    public static final FurnitureSet birch = (FurnitureSet)((FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("birch").surface(BiomeRegistry.PLAINS, BiomeRegistry.DESERT)).cave(BiomeRegistry.PLAINS, BiomeRegistry.DESERT)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.DESERT)).incursion();
    public static final FurnitureSet pine = (FurnitureSet)((FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("pine").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).deepCave(BiomeRegistry.SNOW)).incursion(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
    public static final FurnitureSet palm = (FurnitureSet)((FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("palm").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT)).incursion(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION);
    public static final FurnitureSet dungeon = (FurnitureSet)((FurnitureSet)new FurnitureSet("dungeon").deepCave(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).incursion();
    public static final FurnitureSet deadwood = (FurnitureSet)new FurnitureSet("deadwood").incursion(BiomeRegistry.GRAVEYARD, BiomeRegistry.SPIDER_CASTLE);
    public static final FurnitureSet willow = (FurnitureSet)((FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("willow").surface(BiomeRegistry.SWAMP, BiomeRegistry.FOREST)).cave(BiomeRegistry.SWAMP, BiomeRegistry.FOREST)).deepCave(BiomeRegistry.SWAMP, BiomeRegistry.FOREST)).incursion(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.CRYSTAL_HOLLOW, BiomeRegistry.SLIME_CAVE);
    public static final FurnitureSet bone = (FurnitureSet)((FurnitureSet)new FurnitureSet("bone").deepCave(BiomeRegistry.SNOW)).incursion(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION, BiomeRegistry.SNOW_DEEP_CAVE_INCURSION, BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION);
    public static final FurnitureSet bamboo = (FurnitureSet)((FurnitureSet)new FurnitureSet("bamboo").deepCave(BiomeRegistry.SWAMP)).incursion(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, BiomeRegistry.DESERT_DEEP_CAVE_INCURSION, BiomeRegistry.CRYSTAL_HOLLOW, BiomeRegistry.SLIME_CAVE);
    public static final FurnitureSet dryad = (FurnitureSet)((FurnitureSet)((FurnitureSet)new FurnitureSet("dryad").cave(BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.PLAINS)).incursion(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, BiomeRegistry.SLIME_CAVE, BiomeRegistry.SNOW_DEEP_CAVE_INCURSION);
    public final int chest;
    public final int dinnerTable;
    public final int dinnerTable2;
    public final int desk;
    public final int modularTable;
    public final int chair;
    public final int bench;
    public final int bench2;
    public final int bookshelf;
    public final int cabinet;
    public final int bed;
    public final int bed2;
    public final int doublebed1;
    public final int doublebed2;
    public final int doublebed3;
    public final int doublebed4;
    public final int dresser;
    public final int clock;
    public final int candelabra;
    public final int display;
    public final int bathtub;
    public final int bathtub2;
    public final int toilet;

    public FurnitureSet(String prefixStringID) {
        this.chest = ObjectRegistry.getObjectID(prefixStringID + "chest");
        this.objectArrays = new int[][]{{this.chest}, {this.dinnerTable = ObjectRegistry.getObjectID(prefixStringID + "dinnertable")}, {this.dinnerTable2 = ObjectRegistry.getObjectID(prefixStringID + "dinnertable2")}, {this.desk = ObjectRegistry.getObjectID(prefixStringID + "desk")}, {this.modularTable = ObjectRegistry.getObjectID(prefixStringID + "modulartable")}, {this.chair = ObjectRegistry.getObjectID(prefixStringID + "chair")}, {this.bench = ObjectRegistry.getObjectID(prefixStringID + "bench")}, {this.bench2 = ObjectRegistry.getObjectID(prefixStringID + "bench2")}, {this.bookshelf = ObjectRegistry.getObjectID(prefixStringID + "bookshelf")}, {this.cabinet = ObjectRegistry.getObjectID(prefixStringID + "cabinet")}, {this.bed = ObjectRegistry.getObjectID(prefixStringID + "bed")}, {this.bed2 = ObjectRegistry.getObjectID(prefixStringID + "bed2")}, {this.doublebed1 = ObjectRegistry.getObjectID(prefixStringID + "doublebed")}, {this.doublebed2 = ObjectRegistry.getObjectID(prefixStringID + "doublebed2")}, {this.doublebed3 = ObjectRegistry.getObjectID(prefixStringID + "doublebedfoot1")}, {this.doublebed4 = ObjectRegistry.getObjectID(prefixStringID + "doublebedfoot2")}, {this.dresser = ObjectRegistry.getObjectID(prefixStringID + "dresser")}, {this.clock = ObjectRegistry.getObjectID(prefixStringID + "clock")}, {this.candelabra = ObjectRegistry.getObjectID(prefixStringID + "candelabra")}, {this.display = ObjectRegistry.getObjectID(prefixStringID + "display")}, {this.bathtub = ObjectRegistry.getObjectID(prefixStringID + "bathtub")}, {this.bathtub2 = ObjectRegistry.getObjectID(prefixStringID + "bathtub2")}, {this.toilet = ObjectRegistry.getObjectID(prefixStringID + "toilet")}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(FurnitureSet.class);
    }
}

