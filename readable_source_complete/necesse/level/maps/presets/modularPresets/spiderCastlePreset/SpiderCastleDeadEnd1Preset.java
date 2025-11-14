/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleDeadEnd1Preset
extends SpiderCastlePreset {
    public SpiderCastleDeadEnd1Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [15, spidercastlefloor],\n\ttiles = [15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15],\n\tobjectIDs = [144, barrel, 0, air, 147, spidercastlewallcandle, 55, spidercastlewall, 600, crate, 570, cobweb, 603, vase, 173, spideritearmorstand],\n\tobjects = [55, 55, 55, 55, 55, 55, 55, 55, 600, 600, 147, 603, 600, 55, 55, 144, 0, 0, 0, 600, 55, 55, 173, 0, 0, 0, 144, 55, 55, 570, 0, 0, 0, 0, 55, 55, 600, 0, 0, 0, 570, 55, 55, 55, 0, 0, 0, 55, 55],\n\trotations = [0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 3, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 3, 2, 1, 1, 0, 0, 0, 2, 2],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.open(0, 0, 2);
    }
}

