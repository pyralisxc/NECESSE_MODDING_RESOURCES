/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class ModularTablesPreset
extends Preset {
    public ModularTablesPreset(FurnitureSet set, int frontSpace, int count, boolean includeChairs, Preset.ApplyPredicateFunction wallPredicate) {
        super(count, (includeChairs ? 2 : 1) + frontSpace);
        for (int i = 0; i < count; ++i) {
            this.setObject(i, 0, set.modularTable, 2);
            if (!includeChairs) continue;
            this.setObject(i, 1, set.chair, 0);
        }
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, count - 1, -1, 2, wallPredicate);
        }
    }

    public ModularTablesPreset(FurnitureSet set, int frontSpace, int count, boolean includeChairs) {
        this(set, frontSpace, count, includeChairs, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

