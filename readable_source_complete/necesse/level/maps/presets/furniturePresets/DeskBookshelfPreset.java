/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class DeskBookshelfPreset
extends Preset {
    public DeskBookshelfPreset(FurnitureSet set, int frontSpace, Preset.ApplyPredicateFunction wallPredicate) {
        super(2, 2 + frontSpace);
        this.applyScript("PRESET = {\n\twidth = 2,\n\theight = 2,\n\tobjectIDs = [96, oakbookshelf, 0, air, 91, oakdesk, 93, oakchair],\n\tobjects = [91, 96, 93, 0],\n\trotations = [2, 2, 0, 0]\n}");
        FurnitureSet.oak.replaceWith(set, this);
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 1, -1, 2, wallPredicate);
        }
    }

    public DeskBookshelfPreset(FurnitureSet set, int frontSpace) {
        this(set, frontSpace, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

