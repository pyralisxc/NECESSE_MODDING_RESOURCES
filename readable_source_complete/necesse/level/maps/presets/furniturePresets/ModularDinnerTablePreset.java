/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class ModularDinnerTablePreset
extends Preset {
    public ModularDinnerTablePreset(FurnitureSet set, int frontSpace, int count, Preset.ApplyPredicateFunction wallPredicate) {
        super(3, count + frontSpace);
        for (int i = 0; i < count; ++i) {
            this.setObject(1, i, set.modularTable, 2);
            this.setObject(0, i, set.chair, 1);
            this.setObject(2, i, set.chair, 3);
        }
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, wallPredicate);
        }
    }

    public ModularDinnerTablePreset(FurnitureSet set, int frontSpace, int count) {
        this(set, frontSpace, count, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

