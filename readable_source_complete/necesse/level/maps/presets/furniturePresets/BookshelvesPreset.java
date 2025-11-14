/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class BookshelvesPreset
extends Preset {
    public BookshelvesPreset(FurnitureSet set, int frontSpace, int count, Preset.ApplyPredicateFunction wallPredicate) {
        super(count, 1 + frontSpace);
        for (int i = 0; i < count; ++i) {
            this.setObject(i, 0, set.bookshelf, 2);
        }
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, count - 1, -1, 2, wallPredicate);
        }
    }

    public BookshelvesPreset(FurnitureSet set, int frontSpace, int count) {
        this(set, frontSpace, count, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

