/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class BenchPreset
extends Preset {
    public BenchPreset(FurnitureSet set, int frontSpace, Preset.ApplyPredicateFunction wallPredicate) {
        super(2, 1 + frontSpace);
        this.applyScript("PRESET = {\n\twidth = 2,\n\theight = 1,\n\tobjectIDs = [99, oakbench, 100, oakbench2],\n\tobjects = [99, 100],\n\trotations = [1, 1]\n}");
        FurnitureSet.oak.replaceWith(set, this);
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 1, -1, 2, wallPredicate);
        }
    }

    public BenchPreset(FurnitureSet set, int frontSpace) {
        this(set, frontSpace, (level, levelX, levelY, dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        });
    }
}

