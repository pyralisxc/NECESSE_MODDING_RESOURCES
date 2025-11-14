/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class FenceSet
extends PresetSet<FenceSet> {
    public static final FenceSet wood = (FenceSet)new FenceSet("wood").all();
    public static final FenceSet stone = (FenceSet)new FenceSet("stone").all();
    public static final FenceSet iron = (FenceSet)new FenceSet("iron").all();
    public static final FenceSet crypt = (FenceSet)new FenceSet("crypt").incursion(BiomeRegistry.GRAVEYARD);
    public final int fence;
    public final int gateClosed;
    public final int gateOpen;

    public FenceSet(String prefixStringID) {
        this.fence = ObjectRegistry.getObjectID(prefixStringID + "fence");
        this.objectArrays = new int[][]{{this.fence}, {this.gateClosed = ObjectRegistry.getObjectID(prefixStringID + "fencegate")}, {this.gateOpen = ObjectRegistry.getObjectID(prefixStringID + "fencegateopen")}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(FenceSet.class);
    }
}

