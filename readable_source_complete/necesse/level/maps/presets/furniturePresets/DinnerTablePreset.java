/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class DinnerTablePreset
extends Preset {
    public DinnerTablePreset(FurnitureSet set, int frontSpace, Preset.ApplyPredicateFunction wallPredicate) {
        super(3, 2 + frontSpace);
        this.applyScript("PRESET = {\n\twidth = 3,\n\theight = 2,\n\tobjectIDs = [89, oakdinnertable, 90, oakdinnertable2, 93, oakchair],\n\tobjects = [93, 89, 93, 93, 90, 93],\n\trotations = [1, 2, 3, 1, 2, 3]\n}");
        FurnitureSet.oak.replaceWith(set, this);
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, wallPredicate);
        }
    }

    public DinnerTablePreset(FurnitureSet set, int frontSpace) {
        this(set, frontSpace, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

