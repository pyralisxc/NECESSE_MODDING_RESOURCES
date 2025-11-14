/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class FlowerPatchSet
extends PresetSet<FlowerPatchSet> {
    public static final FlowerPatchSet blue = (FlowerPatchSet)((FlowerPatchSet)((FlowerPatchSet)new FlowerPatchSet("blue").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).deepCave(BiomeRegistry.SNOW);
    public static final FlowerPatchSet yellow;
    public static final FlowerPatchSet red;
    public static final FlowerPatchSet purple;
    public static final FlowerPatchSet white;
    public final int wildFlower;

    public FlowerPatchSet(String prefix) {
        this.wildFlower = ObjectRegistry.getObjectID(prefix + "flowerpatch");
        this.objectArrays = new int[][]{{this.wildFlower}};
    }

    static {
        purple = (FlowerPatchSet)((FlowerPatchSet)((FlowerPatchSet)new FlowerPatchSet("purple").surface(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
        red = (FlowerPatchSet)((FlowerPatchSet)((FlowerPatchSet)new FlowerPatchSet("red").surface(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
        white = (FlowerPatchSet)((FlowerPatchSet)((FlowerPatchSet)new FlowerPatchSet("white").surface(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
        yellow = (FlowerPatchSet)((FlowerPatchSet)((FlowerPatchSet)new FlowerPatchSet("yellow").surface(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
        PresetDebugPreviewForm.registerPresetSet(FlowerPatchSet.class);
    }
}

