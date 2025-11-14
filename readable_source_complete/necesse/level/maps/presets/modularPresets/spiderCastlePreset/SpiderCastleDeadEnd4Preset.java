/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleDeadEnd4Preset
extends SpiderCastlePreset {
    public SpiderCastleDeadEnd4Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [15, spidercastlefloor],\n\ttiles = [15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15],\n\tobjectIDs = [0, air, 273, spidercastlemodulartable, 55, spidercastlewall, 603, vase, 188, spidercastlecandelabra, 173, spideritearmorstand],\n\tobjects = [55, 55, 55, 55, 55, 55, 55, 55, 273, 273, 603, 273, 273, 55, 55, 273, 0, 0, 0, 273, 55, 55, 173, 0, 0, 0, 173, 55, 55, 273, 0, 0, 0, 273, 55, 55, 188, 0, 0, 0, 188, 55, 55, 55, 0, 0, 0, 55, 55],\n\trotations = [1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 2, 0, 3, 1, 1, 0, 0, 2, 0, 0, 1, 1, 0, 0, 2, 0, 0, 1, 1, 1, 0, 2, 0, 1, 1],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.open(0, 0, 2);
    }
}

