/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.furniturePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class SingleChestPreset
extends Preset {
    public SingleChestPreset(FurnitureSet set, int frontSpace, GameRandom random, LootTable lootTable, Preset.ApplyPredicateFunction wallPredicate, Object ... lootParams) {
        super(3, 1 + frontSpace);
        this.applyScript("PRESET = {\n\twidth = 3,\n\theight = 1,\n\tobjectIDs = [0, air, 88, oakchest],\n\tobjects = [0, 88, 0],\n\trotations = [2, 2, 2]\n}");
        FurnitureSet.oak.replaceWith(set, this);
        if (lootTable != null) {
            this.addInventory(lootTable, random, 1, 0, lootParams);
        }
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, wallPredicate);
        }
    }

    public SingleChestPreset(FurnitureSet set, int frontSpace, GameRandom random, LootTable lootTable, Object ... lootParams) {
        this(set, frontSpace, random, lootTable, (Level level, int levelX, int levelY, int dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        }, lootParams);
    }
}

