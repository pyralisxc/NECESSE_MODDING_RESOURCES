/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class SmallPaintingSet
extends PresetSet<SmallPaintingSet> {
    public static final SmallPaintingSet epicCooljonas = new SmallPaintingSet("paintingcooljonas");
    public static final SmallPaintingSet epicElder = new SmallPaintingSet("paintingelder");
    public static final SmallPaintingSet rareStonecaveling = new SmallPaintingSet("paintingstonecaveling");
    public static final SmallPaintingSet rareSnowcaveling = new SmallPaintingSet("paintingsnowcaveling");
    public static final SmallPaintingSet rareSwampcaveling = new SmallPaintingSet("paintingswampcaveling");
    public static final SmallPaintingSet rareSandstonecaveling = new SmallPaintingSet("paintingsandstonecaveling");
    public static final SmallPaintingSet uncommonHeart = new SmallPaintingSet("paintingheart");
    public static final SmallPaintingSet uncommonDagger = new SmallPaintingSet("paintingdagger");
    public static final SmallPaintingSet uncommonEye = new SmallPaintingSet("paintingeye");
    public static final SmallPaintingSet uncommonMouse = new SmallPaintingSet("paintingmouse");
    public static final SmallPaintingSet uncommonParrot = new SmallPaintingSet("paintingparrot");
    public static final SmallPaintingSet uncommonDuck = new SmallPaintingSet("paintingduck");
    public static final SmallPaintingSet uncommonCastle = new SmallPaintingSet("paintingcastle");
    public static final SmallPaintingSet commonApple = new SmallPaintingSet("paintingapple");
    public static final SmallPaintingSet commonAvocado = new SmallPaintingSet("paintingavocado");
    public static final SmallPaintingSet commonBanana = new SmallPaintingSet("paintingbanana");
    public static final SmallPaintingSet commonAbstract = new SmallPaintingSet("paintingabstract");
    public static final SmallPaintingSet commonRainsun = new SmallPaintingSet("paintingrainsun");
    public static final SmallPaintingSet abandonedMineCastle = new SmallPaintingSet("paintingcastle");
    public static final SmallPaintingSet abandonedMineParrot = new SmallPaintingSet("paintingparrot");
    public static final SmallPaintingSet abandonedMineEye = new SmallPaintingSet("paintingeye");
    public static final SmallPaintingSet abandonedMineDagger = new SmallPaintingSet("paintingdagger");
    public static final SmallPaintingSet abandonedMineStonecaveling = new SmallPaintingSet("paintingstonecaveling");
    public static final SmallPaintingSet abandonedMineBroken = new SmallPaintingSet("paintingbroken");
    public final int painting;

    protected SmallPaintingSet(String painting) {
        this.painting = ObjectRegistry.getObjectID(painting);
        this.objectArrays = new int[][]{{this.painting}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(SmallPaintingSet.class);
    }
}

