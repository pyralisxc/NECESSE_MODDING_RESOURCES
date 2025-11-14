/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class FlowerSet
extends PresetSet<FlowerSet> {
    public static final FlowerSet iceblossom = (FlowerSet)((FlowerSet)((FlowerSet)new FlowerSet("iceblossom").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).deepCave(BiomeRegistry.SNOW);
    public static final FlowerSet sunflower = (FlowerSet)((FlowerSet)((FlowerSet)new FlowerSet("sunflower").surface(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
    public static final FlowerSet firemone = (FlowerSet)((FlowerSet)((FlowerSet)new FlowerSet("firemone").surface(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
    public static final FlowerSet mushroom = (FlowerSet)((FlowerSet)((FlowerSet)new FlowerSet("mushroom").surface(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
    public static final FlowerSet caveglow = (FlowerSet)((FlowerSet)new FlowerSet("caveglow").cave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.PLAINS, BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
    public final int flower;
    public final int wildflower;

    public FlowerSet(String objectID) {
        this.flower = ObjectRegistry.getObjectID(objectID);
        this.objectArrays = new int[][]{{this.flower}, {this.wildflower = ObjectRegistry.getObjectID("wild" + objectID)}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(FlowerSet.class);
    }
}

