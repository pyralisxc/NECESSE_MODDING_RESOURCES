/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageWall2Preset
extends PirateVillagePreset {
    public PirateVillageWall2Preset(GameRandom random) {
        super(3, 1, false, random);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 3,\n\ttileIDs = [12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
    }
}

