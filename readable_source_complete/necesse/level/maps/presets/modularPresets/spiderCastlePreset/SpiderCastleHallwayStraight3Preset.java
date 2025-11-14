/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleHallwayStraight3Preset
extends SpiderCastlePreset {
    public SpiderCastleHallwayStraight3Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [15, spidercastlefloor],\n\ttiles = [15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15],\n\tobjectIDs = [0, air, 147, spidercastlewallcandle, 55, spidercastlewall],\n\tobjects = [55, 55, 0, 0, 0, 55, 55, 55, 55, 0, 0, 0, 55, 55, 55, 55, 0, 0, 0, 55, 55, 55, 55, 0, 0, 147, 55, 55, 55, 55, 0, 0, 0, 55, 55, 55, 55, 0, 0, 0, 55, 55, 55, 55, 0, 0, 0, 55, 55],\n\trotations = [1, 1, 0, 0, 0, 1, 1, 3, 2, 0, 0, 0, 2, 1, 3, 2, 0, 0, 0, 2, 3, 3, 2, 0, 0, 1, 2, 3, 3, 2, 0, 0, 0, 2, 3, 1, 2, 0, 0, 0, 2, 3, 1, 1, 0, 0, 3, 2, 2],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.open(0, 0, 0);
        this.open(0, 0, 2);
    }
}

