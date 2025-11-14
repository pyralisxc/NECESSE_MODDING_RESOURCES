/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class BushSet
extends PresetSet<BushSet> {
    public static final BushSet blueberry = (BushSet)new BushSet("blueberry").surface(BiomeRegistry.FOREST);
    public static final BushSet raspberry = (BushSet)new BushSet("raspberry").surface(BiomeRegistry.PLAINS);
    public static final BushSet blackberry = (BushSet)new BushSet("blackberry").surface(BiomeRegistry.SNOW);
    public final int bush;
    public final int sapling;

    public BushSet(String prefixStringID) {
        this.bush = ObjectRegistry.getObjectID(prefixStringID + "bush");
        this.objectArrays = new int[][]{{this.bush}, {this.sapling = ObjectRegistry.getObjectID(prefixStringID + "sapling")}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(BushSet.class);
    }
}

