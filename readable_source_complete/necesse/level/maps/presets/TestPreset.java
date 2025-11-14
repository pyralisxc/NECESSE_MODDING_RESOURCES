/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.level.maps.presets.Preset;

public class TestPreset
extends Preset {
    public TestPreset() {
        super(2, 2);
        this.applyScript("PRESET = {\n\twidth = 2,\n\theight = 2,\n\ttileIDs = [3, grasstile],\n\ttiles = [3, 3, 3, 3],\n\tobjectIDs = [0, air, 43, carpentersbench, 44, carpentersbench2],\n\tobjects = [43, 44, 0, 0],\n\trotations = [1, 1, 1, 3],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
    }
}

