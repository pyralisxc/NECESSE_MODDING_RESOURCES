/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class HedgeSet
extends PresetSet<HedgeSet> {
    public static final HedgeSet forest = (HedgeSet)new HedgeSet("forest").surface(BiomeRegistry.FOREST);
    public static final HedgeSet snow = (HedgeSet)new HedgeSet("snow").surface(BiomeRegistry.SNOW);
    public static final HedgeSet plains = (HedgeSet)new HedgeSet("plains").surface(BiomeRegistry.PLAINS);
    public static final HedgeSet swamp = (HedgeSet)new HedgeSet("swamp").surface(BiomeRegistry.SWAMP);
    public final int hedge;
    public final int hedgegate;

    public HedgeSet(String prefixStringID) {
        this.hedge = ObjectRegistry.getObjectID(prefixStringID + "hedge");
        this.objectArrays = new int[][]{{this.hedge}, {this.hedgegate = ObjectRegistry.getObjectID(prefixStringID + "hedgegate")}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(HedgeSet.class);
    }
}

