/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillagePathPreset
extends VillagePreset {
    public VillagePathPreset() {
        this(true, true, true, true);
    }

    public VillagePathPreset(boolean openTop, boolean openRight, boolean openBottom, boolean openLeft) {
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

