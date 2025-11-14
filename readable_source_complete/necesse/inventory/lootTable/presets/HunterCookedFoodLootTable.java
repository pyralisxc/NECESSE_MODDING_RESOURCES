/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class HunterCookedFoodLootTable
extends LootTable {
    public static final HunterCookedFoodLootTable instance = new HunterCookedFoodLootTable();

    private HunterCookedFoodLootTable() {
        super(LootItem.between("roastedfish", 1, 3), LootItem.between("roastedrabbitleg", 4, 9), LootItem.between("roastedduckbreast", 2, 6));
    }
}

