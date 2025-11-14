/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class PotAndPlantSet
extends PresetSet<PotAndPlantSet> {
    public static final PotAndPlantSet decorativePot1 = new PotAndPlantSet("decorativepot1");
    public static final PotAndPlantSet decorativePot2 = new PotAndPlantSet("decorativepot2");
    public static final PotAndPlantSet decorativePot3 = new PotAndPlantSet("decorativepot3");
    public static final PotAndPlantSet decorativePot4 = new PotAndPlantSet("decorativepot4");
    public static final PotAndPlantSet pottedCactus1 = new PotAndPlantSet("pottedcactus1");
    public static final PotAndPlantSet pottedCactus2 = new PotAndPlantSet("pottedcactus2");
    public static final PotAndPlantSet pottedCactus3 = new PotAndPlantSet("pottedcactus3");
    public static final PotAndPlantSet pottedCactus4 = new PotAndPlantSet("pottedcactus4");
    public static final PotAndPlantSet pottedFlower1 = new PotAndPlantSet("pottedflower1");
    public static final PotAndPlantSet pottedFlower2 = new PotAndPlantSet("pottedflower2");
    public static final PotAndPlantSet pottedFlower3 = new PotAndPlantSet("pottedflower3");
    public static final PotAndPlantSet pottedFlower4 = new PotAndPlantSet("pottedflower4");
    public static final PotAndPlantSet pottedFlower5 = new PotAndPlantSet("pottedflower5");
    public static final PotAndPlantSet pottedFlower6 = new PotAndPlantSet("pottedflower6");
    public static final PotAndPlantSet pottedPlant1 = new PotAndPlantSet("pottedplant1");
    public static final PotAndPlantSet pottedPlant2 = new PotAndPlantSet("pottedplant2");
    public static final PotAndPlantSet pottedPlant3 = new PotAndPlantSet("pottedplant3");
    public static final PotAndPlantSet pottedPlant4 = new PotAndPlantSet("pottedplant4");
    public static final PotAndPlantSet pottedPlant5 = new PotAndPlantSet("pottedplant5");
    public static final PotAndPlantSet pottedPlant6 = new PotAndPlantSet("pottedplant6");
    public static final PotAndPlantSet pottedPlant7 = new PotAndPlantSet("pottedplant7");
    public final int pot;

    protected PotAndPlantSet(String pot) {
        this.pot = ObjectRegistry.getObjectID(pot);
        this.objectArrays = new int[][]{{this.pot}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(PotAndPlantSet.class);
    }
}

