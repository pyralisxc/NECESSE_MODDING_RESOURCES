/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class BookshelfClockPreset
extends Preset {
    public BookshelfClockPreset(FurnitureSet set, int frontSpace, Preset.ApplyPredicateFunction wallPredicate) {
        super(3, 1 + frontSpace);
        this.applyScript("PRESET = {\n\twidth = 3,\n\theight = 1,\n\tobjectIDs = [96, oakbookshelf, 101, oakclock],\n\tobjects = [96, 101, 96],\n\trotations = [2, 2, 2]\n}");
        FurnitureSet.oak.replaceWith(set, this);
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, wallPredicate);
        }
    }

    public BookshelfClockPreset(FurnitureSet set, int frontSpace) {
        this(set, frontSpace, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

