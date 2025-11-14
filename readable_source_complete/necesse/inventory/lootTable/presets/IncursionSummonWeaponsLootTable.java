/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionSummonWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionSummonWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionSummonWeaponsLootTable instance = new IncursionSummonWeaponsLootTable();

    private IncursionSummonWeaponsLootTable() {
        super(new LootItemInterface[]{incursionSummonWeapons});
    }
}

