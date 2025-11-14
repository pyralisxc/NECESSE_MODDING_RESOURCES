/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionCloseRangeWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionCloseRangeWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionCloseRangeWeaponsLootTable instance = new IncursionCloseRangeWeaponsLootTable();

    private IncursionCloseRangeWeaponsLootTable() {
        super(new LootItemInterface[]{incursionCloseRangeWeapons});
    }
}

