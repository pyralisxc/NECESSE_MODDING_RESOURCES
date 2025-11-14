/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class CloseRangeWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems closeRangeWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final CloseRangeWeaponsLootTable instance = new CloseRangeWeaponsLootTable();

    private CloseRangeWeaponsLootTable() {
        super(new LootItemInterface[]{closeRangeWeapons});
    }
}

