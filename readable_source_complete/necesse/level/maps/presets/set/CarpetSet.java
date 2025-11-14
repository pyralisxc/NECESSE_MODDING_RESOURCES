/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class CarpetSet
extends PresetSet<CarpetSet> {
    public static final CarpetSet wool = (CarpetSet)new CarpetSet("woolcarpet").all();
    public static final CarpetSet leather = (CarpetSet)new CarpetSet("leathercarpet").all();
    public static final CarpetSet brownbear = (CarpetSet)new CarpetSet("brownbearcarpet").all();
    public static final CarpetSet blue = (CarpetSet)new CarpetSet("bluecarpet").all();
    public static final CarpetSet goldgrid = (CarpetSet)new CarpetSet("goldgridcarpet").all();
    public static final CarpetSet green = (CarpetSet)new CarpetSet("greencarpet").all();
    public static final CarpetSet steelgrey = (CarpetSet)new CarpetSet("steelgreycarpet").all();
    public static final CarpetSet purple = (CarpetSet)new CarpetSet("purplecarpet").all();
    public static final CarpetSet velour = (CarpetSet)new CarpetSet("velourcarpet").all();
    public static final CarpetSet heart = (CarpetSet)new CarpetSet("heartcarpet").all();
    public static final CarpetSet redyarn = (CarpetSet)new CarpetSet("redyarncarpet").all();
    public final int carpet;

    protected CarpetSet(String carpet) {
        this.carpet = ObjectRegistry.getObjectID(carpet);
        this.objectArrays = new int[][]{{this.carpet}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(CarpetSet.class);
    }
}

