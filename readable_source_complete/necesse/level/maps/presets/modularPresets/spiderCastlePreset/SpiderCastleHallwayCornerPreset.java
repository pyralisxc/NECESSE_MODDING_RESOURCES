/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleHallwayCornerPreset
extends SpiderCastlePreset {
    public SpiderCastleHallwayCornerPreset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [55, spidercastlefloor],\n\ttiles = [55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55],\n\tobjectIDs = [0, air, 194, spidercastlecandelabra, 148, spidercastlecolumn, 120, spidercastlewall, 222, spidercastlemodulartable],\n\tobjects = [120, 120, 0, 0, 0, 120, 120, 120, 222, 0, 0, 0, 148, 120, 0, 0, 0, 0, 0, 0, 120, 0, 0, 0, 0, 0, 0, 120, 0, 0, 0, 0, 194, 120, 120, 120, 148, 0, 0, 120, 120, 120, 120, 120, 120, 120, 120, 120, 120],\n\trotations = [0, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.open(0, 0, 0);
        this.open(0, 0, 1);
    }
}

