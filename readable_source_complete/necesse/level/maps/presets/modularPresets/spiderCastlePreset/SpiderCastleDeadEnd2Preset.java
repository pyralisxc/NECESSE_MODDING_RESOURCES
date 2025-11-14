/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleDeadEnd2Preset
extends SpiderCastlePreset {
    public SpiderCastleDeadEnd2Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [15, spidercastlefloor],\n\ttiles = [15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15],\n\tobjectIDs = [0, air, 147, spidercastlewallcandle, 55, spidercastlewall, 600, crate, 570, cobweb],\n\tobjects = [55, 55, 55, 55, 55, 55, 55, 55, 570, 570, 147, 600, 600, 55, 55, 570, 0, 0, 0, 570, 55, 55, 600, 0, 570, 0, 600, 55, 55, 600, 0, 0, 0, 570, 55, 55, 570, 0, 0, 570, 570, 55, 55, 55, 0, 0, 0, 55, 55],\n\trotations = [0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 3, 3, 1, 1, 1, 0, 0, 0, 1, 1, 1, 3, 0, 1, 0, 3, 1, 1, 3, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.open(0, 0, 2);
    }
}

