/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillagePathPreset
extends PirateVillagePreset {
    public PirateVillagePathPreset() {
        this(true, true, true, true);
    }

    public PirateVillagePathPreset(boolean openTop, boolean openRight, boolean openBottom, boolean openLeft) {
        super(1, 1, true);
        this.fillTile(0, 0, this.width, this.height, this.path);
        this.fillObject(0, 0, this.width, this.height, 0);
        if (openTop) {
            this.open(0, 0, 0);
        }
        if (openRight) {
            this.open(0, 0, 1);
        }
        if (openBottom) {
            this.open(0, 0, 2);
        }
        if (openLeft) {
            this.open(0, 0, 3);
        }
    }
}

