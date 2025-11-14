/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class GroundSet
extends PresetSet<GroundSet> {
    public static final GroundSet forest = (GroundSet)((GroundSet)((GroundSet)new GroundSet("grass", "air", "grasstile", "overgrowngrasstile", "graveltile").surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST)).incursion();
    public static final GroundSet swamp = (GroundSet)((GroundSet)((GroundSet)new GroundSet("swampgrass", "deepswamptallgrass", "swampgrasstile", "overgrownswampgrasstile", "graveltile").surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP)).incursion();
    public static final GroundSet plains = (GroundSet)((GroundSet)((GroundSet)new GroundSet("plainsgrass", "air", "plainsgrasstile", "overgrownplainsgrasstile", "graveltile").surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS)).incursion();
    public static final GroundSet snow = (GroundSet)((GroundSet)((GroundSet)new GroundSet("air", "air", "snowtile", "snowtile", "graveltile").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).incursion();
    public static final GroundSet desert = (GroundSet)((GroundSet)((GroundSet)new GroundSet("air", "air", "sandtile", "sandtile", "sandgraveltile").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).incursion();
    public final int grassObject;
    public final int tallGrassObject;
    public final int grassTile;
    public final int overgrownGrassTile;
    public final int gravelTile;

    public GroundSet(String grassObject, String tallGrassObject, String grassTile, String overgrownGrassTile, String gravelTile) {
        this.grassObject = ObjectRegistry.getObjectID(grassObject);
        this.objectArrays = new int[][]{{this.grassObject}, {this.tallGrassObject = ObjectRegistry.getObjectID(tallGrassObject)}};
        this.grassTile = TileRegistry.getTileID(grassTile);
        this.tileArrays = new int[][]{{this.grassTile}, {this.overgrownGrassTile = TileRegistry.getTileID(overgrownGrassTile)}, {this.gravelTile = TileRegistry.getTileID(gravelTile)}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(GroundSet.class);
    }
}

