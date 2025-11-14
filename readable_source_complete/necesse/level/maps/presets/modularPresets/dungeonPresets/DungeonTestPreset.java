/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.dungeonPresets;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonPreset;

public class DungeonTestPreset
extends DungeonPreset {
    public DungeonTestPreset(GameRandom random) {
        super(1, 1, random);
        this.open(0, 0, 0);
        this.open(0, 0, 1);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
    }
}

