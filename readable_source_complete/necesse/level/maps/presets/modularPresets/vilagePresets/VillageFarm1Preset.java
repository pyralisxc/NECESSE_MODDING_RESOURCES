/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageFarm1Preset
extends VillagePreset {
    public VillageFarm1Preset(GameRandom random) {
        super(3, 4, false, random);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 12,\n\ttileIDs = [18, stonepathtile, 9, farmland],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, -1, 18, -1, 18, -1, -1, -1, -1, -1, -1, 18, 18, 18, -1, -1, -1],\n\tobjectIDs = [0, air, 67, sign, 228, wheatseed, 55, woodfence, 200, sunflowerseed, 207, firemoneseed],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 55, 55, 55, 55, 55, 55, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 55, 0, 67, 0, 55, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0]\n}");
        PresetUtils.applyRandomSeedArea(this, 2, 2, 1, 8, random);
        PresetUtils.applyRandomSeedArea(this, 4, 2, 1, 8, random);
        PresetUtils.applyRandomSeedArea(this, 6, 2, 1, 8, random);
        this.addSign("Community farm", 4, 10);
        this.open(1, 3, 2);
    }
}

