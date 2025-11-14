/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class PottedPlantSet
extends PresetSet<PottedPlantSet> {
    public static final PottedPlantSet pottedFlower1 = (PottedPlantSet)new PottedPlantSet("pottedflower1").all();
    public static final PottedPlantSet pottedFlower2 = (PottedPlantSet)new PottedPlantSet("pottedflower2").all();
    public static final PottedPlantSet pottedFlower3 = (PottedPlantSet)new PottedPlantSet("pottedflower3").all();
    public static final PottedPlantSet pottedFlower4 = (PottedPlantSet)new PottedPlantSet("pottedflower4").all();
    public static final PottedPlantSet pottedFlower5 = (PottedPlantSet)new PottedPlantSet("pottedflower5").all();
    public static final PottedPlantSet pottedFlower6 = (PottedPlantSet)new PottedPlantSet("pottedflower6").all();
    public static final PottedPlantSet pottedCactus1 = (PottedPlantSet)new PottedPlantSet("pottedcactus1").all();
    public static final PottedPlantSet pottedCactus2 = (PottedPlantSet)new PottedPlantSet("pottedcactus2").all();
    public static final PottedPlantSet pottedCactus3 = (PottedPlantSet)new PottedPlantSet("pottedcactus3").all();
    public static final PottedPlantSet pottedCactus4 = (PottedPlantSet)new PottedPlantSet("pottedcactus4").all();
    public static final PottedPlantSet pottedPlant1 = (PottedPlantSet)new PottedPlantSet("pottedplant1").all();
    public static final PottedPlantSet pottedPlant2 = (PottedPlantSet)new PottedPlantSet("pottedplant2").all();
    public static final PottedPlantSet pottedPlant3 = (PottedPlantSet)new PottedPlantSet("pottedplant3").all();
    public static final PottedPlantSet pottedPlant4 = (PottedPlantSet)new PottedPlantSet("pottedplant4").all();
    public static final PottedPlantSet pottedPlant5 = (PottedPlantSet)new PottedPlantSet("pottedplant5").all();
    public static final PottedPlantSet pottedPlant6 = (PottedPlantSet)new PottedPlantSet("pottedplant6").all();
    public static final PottedPlantSet pottedPlant7 = (PottedPlantSet)new PottedPlantSet("pottedplant7").all();
    public final int pottedItem;

    public PottedPlantSet(String objectID) {
        this.pottedItem = ObjectRegistry.getObjectID(objectID);
        this.objectArrays = new int[][]{{this.pottedItem}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(PottedPlantSet.class);
    }
}

