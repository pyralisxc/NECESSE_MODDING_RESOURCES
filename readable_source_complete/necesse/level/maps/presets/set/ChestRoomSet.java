/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import java.util.Arrays;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class ChestRoomSet
extends PresetSet<ChestRoomSet> {
    public static final ChestRoomSet wood = (ChestRoomSet)((ChestRoomSet)((ChestRoomSet)((ChestRoomSet)new ChestRoomSet("stonefloor", "stonepressureplate", WallSet.wood, ColumnSet.wood, "storagebox", "woodarrowtrap").surface(BiomeRegistry.FOREST, BiomeRegistry.PLAINS)).cave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS)).incursion();
    public static final ChestRoomSet dryad = (ChestRoomSet)((ChestRoomSet)new ChestRoomSet("granitefloor", "granitepressureplate", WallSet.dryad, ColumnSet.sandstone, "storagebox", "dryadarrowtrap").surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS);
    public static final ChestRoomSet stone = (ChestRoomSet)((ChestRoomSet)((ChestRoomSet)new ChestRoomSet("stonefloor", "stonepressureplate", WallSet.stone, ColumnSet.stone, "storagebox", "stoneflametrap", "stonearrowtrap").surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST)).incursion(BiomeRegistry.FOREST);
    public static final ChestRoomSet sandstone = (ChestRoomSet)((ChestRoomSet)new ChestRoomSet("sandstonefloor", "sandstonepressureplate", WallSet.sandstone, ColumnSet.sandstone, "storagebox", "sandstoneflametrap", "sandstonearrowtrap").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT);
    public static final ChestRoomSet snowStone = (ChestRoomSet)((ChestRoomSet)new ChestRoomSet("snowstonefloor", "snowstonepressureplate", WallSet.snowStone, ColumnSet.snowstone, "storagebox", "snowstonearrowtrap").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW);
    public static final ChestRoomSet ice = (ChestRoomSet)((ChestRoomSet)new ChestRoomSet("snowstonefloor", "snowstonepressureplate", WallSet.ice, ColumnSet.deepstone, "storagebox", "icearrowtrap").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW);
    public static final ChestRoomSet granite = (ChestRoomSet)((ChestRoomSet)new ChestRoomSet("granitefloor", "granitepressureplate", WallSet.granite, ColumnSet.sandstone, "storagebox", "graniteflametrap", "granitearrowtrap").surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS);
    public static final ChestRoomSet swampStone = (ChestRoomSet)((ChestRoomSet)new ChestRoomSet("swampstonefloor", "swampstonepressureplate", WallSet.swampStone, ColumnSet.swampstone, "storagebox", "swampstoneflametrap", "swampstonearrowtrap").surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP);
    public static final ChestRoomSet deepStone = (ChestRoomSet)new ChestRoomSet("deepstonefloor", "deepstonepressureplate", WallSet.deepStone, ColumnSet.deepstone, "storagebox", "deepstoneflametrap", "deepstonearrowtrap").deepCave(BiomeRegistry.FOREST);
    public static final ChestRoomSet obsidian = (ChestRoomSet)new ChestRoomSet("deepstonefloor", "deepstonepressureplate", WallSet.obsidian, ColumnSet.obsidian, "storagebox", "obsidianflametrap", "obsidianarrowtrap").deepCave(BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.FOREST);
    public static final ChestRoomSet deepSnowStone = (ChestRoomSet)new ChestRoomSet("deepsnowstonefloor", "deepsnowstonepressureplate", WallSet.deepSnowStone, ColumnSet.deepsnowstone, "storagebox", "deepsnowstoneflametrap", "deepsnowstonearrowtrap").deepCave(BiomeRegistry.SNOW);
    public static final ChestRoomSet basalt = (ChestRoomSet)new ChestRoomSet("basaltfloor", "basaltpressureplate", WallSet.basalt, ColumnSet.basalt, "storagebox", "basaltflametrap", "basaltarrowtrap").deepCave(BiomeRegistry.PLAINS);
    public static final ChestRoomSet deepSwampStone = (ChestRoomSet)new ChestRoomSet("deepswampstonefloor", "deepswampstonepressureplate", WallSet.deepSwampStone, ColumnSet.deepswampstone, "storagebox", "deepswampstoneflametrap", "deepswampstonearrowtrap").deepCave(BiomeRegistry.SWAMP);
    public static final ChestRoomSet deepSandstone = (ChestRoomSet)new ChestRoomSet("woodfloor", "woodpressureplate", WallSet.deepSandstone, ColumnSet.deepsandstone, "storagebox", "deepsandstoneflametrap", "deepsandstonearrowtrap").deepCave(BiomeRegistry.DESERT);
    public final int floor;
    public final int pressureplate;
    public final WallSet wallSet;
    public final ColumnSet columnSet;
    public final int inventoryObject;
    public final int[] traps;

    public ChestRoomSet(String floorStringID, String pressureplateStringID, WallSet wallSet, ColumnSet columnSet, String inventoryObjectStringID, String ... trapsStringIDs) {
        int[][] nArrayArray = new int[4][];
        this.floor = TileRegistry.getTileID(floorStringID);
        nArrayArray[0] = new int[]{this.floor};
        this.pressureplate = ObjectRegistry.getObjectID(pressureplateStringID);
        nArrayArray[1] = new int[]{this.pressureplate};
        this.inventoryObject = ObjectRegistry.getObjectID(inventoryObjectStringID);
        nArrayArray[2] = new int[]{this.inventoryObject};
        this.traps = Arrays.stream(trapsStringIDs).mapToInt(ObjectRegistry::getObjectID).toArray();
        nArrayArray[3] = this.traps;
        this.objectArrays = nArrayArray;
        PresetSet[][] presetSetArrayArray = new PresetSet[2][];
        PresetSet[] presetSetArray = new PresetSet[1];
        this.wallSet = wallSet;
        presetSetArray[0] = this.wallSet;
        presetSetArrayArray[0] = presetSetArray;
        PresetSet[] presetSetArray2 = new PresetSet[1];
        this.columnSet = columnSet;
        presetSetArray2[0] = this.columnSet;
        presetSetArrayArray[1] = presetSetArray2;
        this.nestedSetArrays = presetSetArrayArray;
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(ChestRoomSet.class);
    }
}

