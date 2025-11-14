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

public class DisplayStandClockPreset
extends Preset {
    public DisplayStandClockPreset(FurnitureSet set, int frontSpace, GameRandom random, LootTable lootTable, Preset.ApplyPredicateFunction wallPredicate, Object ... lootParams) {
        super(2, 1 + frontSpace);
        this.applyScript("PRESET = {\n\twidth = 2,\n\theight = 1,\n\tobjectIDs = [101, oakclock, 103, oakdisplay],\n\tobjects = [103, 101],\n\trotations = [2, 2]\n}");
        FurnitureSet.oak.replaceWith(set, this);
        if (lootTable != null) {
            this.addInventory(lootTable, random, 0, 0, lootParams);
        }
        if (wallPredicate != null) {
            this.addCanApplyAreaEachPredicate(0, -1, 1, -1, 2, wallPredicate);
        }
    }

    public DisplayStandClockPreset(FurnitureSet set, int frontSpace, GameRandom random, LootTable lootTable, Object ... lootParams) {
        this(set, frontSpace, random, lootTable, (Level level, int levelX, int levelY, int dir) -> {
            GameObject object = level.getObject(levelX, levelY);
            return object.isWall && !object.isDoor;
        }, lootParams);
    }
}

