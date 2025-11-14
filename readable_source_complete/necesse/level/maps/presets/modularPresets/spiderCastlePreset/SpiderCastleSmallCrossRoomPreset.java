/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleSmallCrossRoomPreset
extends SpiderCastlePreset {
    public SpiderCastleSmallCrossRoomPreset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [18, spidercastlecarpet, 15, spidercastlefloor],\n\ttiles = [15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 18, 18, 18, 18, 18, 15, 15, 18, 18, 18, 18, 18, 15, 15, 18, 18, 18, 18, 18, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15],\n\tobjectIDs = [0, air, 147, spidercastlewallcandle, 55, spidercastlewall],\n\tobjects = [55, 55, 0, 0, 0, 55, 55, 55, 147, 0, 0, 0, 147, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 55, 147, 0, 0, 0, 147, 55, 55, 55, 0, 0, 0, 55, 55],\n\trotations = [1, 1, 3, 3, 3, 1, 1, 1, 3, 0, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 3, 3, 0, 0, 0, 1, 3, 3, 3, 0, 0, 0, 3, 3],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.open(0, 0, 0);
        this.open(0, 0, 1);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
    }
}

