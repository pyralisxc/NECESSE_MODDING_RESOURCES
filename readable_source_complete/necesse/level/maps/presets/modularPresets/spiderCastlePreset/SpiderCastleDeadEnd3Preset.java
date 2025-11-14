/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleDeadEnd3Preset
extends SpiderCastlePreset {
    public SpiderCastleDeadEnd3Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [18, spidercastlecarpet, 15, spidercastlefloor],\n\ttiles = [15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 18, 18, 18, 15, 15, 15, 15, 18, 18, 18, 15, 15, 15, 15, 18, 18, 18, 15, 15, 15, 15, 18, 18, 18, 15, 15, 15, 15, 15, 15, 15, 15, 15],\n\tobjectIDs = [0, air, 273, spidercastlemodulartable, 274, spidercastlechair, 277, spidercastlebookshelf, 55, spidercastlewall, 188, spidercastlecandelabra, 173, spideritearmorstand],\n\tobjects = [55, 55, 55, 55, 55, 55, 55, 55, 188, 277, 173, 277, 188, 55, 55, 273, 0, 0, 274, 273, 55, 55, 273, 274, 0, 0, 273, 55, 55, 273, 0, 0, 0, 273, 55, 55, 277, 0, 0, 0, 277, 55, 55, 55, 0, 0, 0, 55, 55],\n\trotations = [1, 1, 1, 1, 1, 0, 0, 1, 2, 2, 2, 2, 2, 1, 1, 0, 0, 0, 2, 0, 1, 1, 0, 3, 2, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 3, 0, 1, 1, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.open(0, 0, 2);
    }
}

