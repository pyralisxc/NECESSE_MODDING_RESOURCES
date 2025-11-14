/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class VillageSet
extends PresetSet<VillageSet> {
    public static final VillageSet defaultSet = (VillageSet)new VillageSet(FurnitureSet.spruce, WallSet.wood, WallSet.stone, "grasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor").all();
    public static final VillageSet oak = (VillageSet)new VillageSet(FurnitureSet.oak, WallSet.wood, WallSet.stone, "grasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor").surface(BiomeRegistry.FOREST);
    public static final VillageSet spruce = (VillageSet)new VillageSet(FurnitureSet.spruce, WallSet.wood, WallSet.stone, "grasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor").surface(BiomeRegistry.FOREST);
    public static final VillageSet maple = (VillageSet)new VillageSet(FurnitureSet.maple, WallSet.wood, WallSet.stone, "plainsgrasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor").surface(BiomeRegistry.PLAINS);
    public static final VillageSet birch = (VillageSet)new VillageSet(FurnitureSet.birch, WallSet.wood, WallSet.stone, "plainsgrasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor").surface(BiomeRegistry.PLAINS);
    public static final VillageSet pine = (VillageSet)new VillageSet(FurnitureSet.pine, WallSet.pine, WallSet.snowStone, "snowtile", "icetile", "snowstonepathtile", "snowstonefloor", "pinefloor").surface(BiomeRegistry.SNOW);
    public static final VillageSet palm = (VillageSet)new VillageSet(FurnitureSet.palm, WallSet.palm, WallSet.sandstone, "sandtile", "sandtile", "sandstonepathtile", "sandstonefloor", "palmfloor").surface(BiomeRegistry.DESERT);
    public final FurnitureSet furniture;
    public final WallSet woodWalls;
    public final WallSet stoneWalls;
    public final int terrainTile;
    public final int shoreTile;
    public final int pathTile;
    public final int rockTile;
    public final int woodTile;

    public VillageSet(FurnitureSet furniture, WallSet woodWalls, WallSet stoneWalls, String terrainTileStringID, String shoreTileStringID, String pathTileStringID, String rockTileStringID, String woodTileStringID) {
        PresetSet[][] presetSetArrayArray = new PresetSet[3][];
        PresetSet[] presetSetArray = new PresetSet[1];
        this.furniture = furniture;
        presetSetArray[0] = this.furniture;
        presetSetArrayArray[0] = presetSetArray;
        PresetSet[] presetSetArray2 = new PresetSet[1];
        this.woodWalls = woodWalls;
        presetSetArray2[0] = this.woodWalls;
        presetSetArrayArray[1] = presetSetArray2;
        PresetSet[] presetSetArray3 = new PresetSet[1];
        this.stoneWalls = stoneWalls;
        presetSetArray3[0] = this.stoneWalls;
        presetSetArrayArray[2] = presetSetArray3;
        this.nestedSetArrays = presetSetArrayArray;
        this.terrainTile = TileRegistry.getTileID(terrainTileStringID);
        this.tileArrays = new int[][]{{this.terrainTile}, {this.shoreTile = TileRegistry.getTileID(shoreTileStringID)}, {this.pathTile = TileRegistry.getTileID(pathTileStringID)}, {this.rockTile = TileRegistry.getTileID(rockTileStringID)}, {this.woodTile = TileRegistry.getTileID(woodTileStringID)}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(VillageSet.class);
    }
}

