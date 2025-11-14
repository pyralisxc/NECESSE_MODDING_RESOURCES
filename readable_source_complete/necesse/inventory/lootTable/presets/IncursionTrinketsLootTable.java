/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionTrinketsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionTrinkets = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionTrinketsLootTable instance = new IncursionTrinketsLootTable();

    private IncursionTrinketsLootTable() {
        super(new LootItemInterface[]{incursionTrinkets});
    }
}

