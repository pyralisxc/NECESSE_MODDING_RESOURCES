/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class BedDresserPreset
extends Preset {
    public BedDresserPreset(FurnitureSet set, int frontSpace, Preset.ApplyPredicateFunction wallPredicate) {
        super(3, 1 + frontSpace);
        this.applyScript("PRESET = {\n\twidth = 3,\n\theight = 1,\n\tobjectIDs = [98, oakbed, 99, oakbed2, 100, oakdresser],\n\tobjects = [99, 98, 100],\n\trotations = [3, 3, 2]\n}");
        FurnitureSet.oak.replaceWith(set, this);
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, wallPredicate);
        }
    }

    public BedDresserPreset(FurnitureSet set, int frontSpace) {
        this(set, frontSpace, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

