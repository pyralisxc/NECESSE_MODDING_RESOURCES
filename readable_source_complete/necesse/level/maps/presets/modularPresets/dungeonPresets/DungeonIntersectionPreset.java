/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.dungeonPresets;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonPreset;

public class DungeonIntersectionPreset
extends DungeonPreset {
    public DungeonIntersectionPreset(GameRandom random) {
        super(1, 1, random);
        this.fillObject(0, 0, this.width, this.height, this.wall);
        this.fillObject(5, 2, 5, 11, 0);
        this.fillObject(4, 3, 7, 9, 0);
        this.fillObject(3, 4, 9, 7, 0);
        this.fillObject(2, 5, 11, 5, 0);
        this.fillObject(6, 6, 3, 3, this.wall);
        this.fillObject(5, 7, 5, 1, this.wall);
        this.fillObject(7, 5, 1, 5, this.wall);
        this.open(0, 0, 0);
        this.open(0, 0, 1);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
        this.indicatePlaceForRandomSpellTrap(4, 4);
        this.indicatePlaceForRandomSpellTrap(4, 10);
        this.indicatePlaceForRandomSpellTrap(10, 4);
        this.indicatePlaceForRandomSpellTrap(10, 10);
    }
}

