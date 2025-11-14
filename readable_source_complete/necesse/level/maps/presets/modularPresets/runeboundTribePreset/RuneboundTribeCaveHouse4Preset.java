/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;

public class RuneboundTribeCaveHouse4Preset
extends RuneboundTribePreset {
    public RuneboundTribeCaveHouse4Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, dryadfloor, 41, graniterocktile],\n\ttiles = [-1, 41, 41, -1, -1, -1, -1, 41, 41, 41, -1, -1, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 41, 17, 41, 41, 41, -1, -1, 41, 41, 41, 41, 17, 41, 41, 41, 41, -1, -1, -1, 41, 41, 17, 17, 17, 41, 41, 17, 41, -1, -1, -1, -1, 41, 17, 17, 41, 41, 17, 17, 41, 41, -1, -1, -1, 41, 41, 41, 17, 41, 41, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 17, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 260, wallcandle, 808, graniterock, 265, tikitorch, 811, granitecaverocksmall, 428, mapledinnertable, 429, mapledinnertable2, 432, maplechair, 81, dryadwall, 82, dryaddoor, 437, maplebed, 502, bonebench, 438, maplebed2, 503, bonebench2],\n\tobjects = [-1, 808, 808, -1, -1, -1, -1, 808, 808, 808, -1, -1, 808, 808, 808, 808, -1, 808, 808, 808, 808, 808, 808, -1, 808, 808, 808, 808, 808, 808, 808, 808, 808, 808, 808, -1, 808, 808, 808, 808, 808, 81, 428, 429, 808, 808, 808, -1, -1, 808, 808, 81, 0, 260, 0, 432, 81, 808, -1, -1, -1, 808, 808, 503, 0, 0, 0, 0, 437, 808, -1, -1, -1, -1, 808, 502, 0, 0, 0, 0, 438, 808, 808, -1, -1, -1, 808, 808, 81, 0, 0, 260, 81, 808, 808, -1, -1, 808, 808, 808, 808, 808, 808, 82, 808, 808, 808, -1, -1, 808, 808, 808, 808, 808, 811, 0, 808, 808, 811, -1, -1, -1, 808, 808, 808, 265, 0, 0, 811, 0, 0, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, -1, -1],\n\trotations = [3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 1, 1, 2, 2, 2, 3, 3, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 0, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 0, 2, 0, 3, 1, 1, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 2, 0, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0, 2, 0, 1, 2, 2, 0, 2, 2, 1, 0, 1, 3, 1, 2, 1, 2, 2, 2, 2, 2, 3, 3, 1, 2, 3, 1, 2, 2, 2, 2, 2, 0, 1, 1, 2, 0, 2, 1, 3, 2, 2, 2, 0, 0, 1, 3, 0, 0, 3, 0, 3, 2, 3, 3, 3, 3, 1, 0, 0, 0, 0, 3, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.open(0, 0, 2);
        this.addRuneboundMobSpawn(6, 6);
    }
}

