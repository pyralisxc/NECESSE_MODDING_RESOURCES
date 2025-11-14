/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class RareIncursionTrinketsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems rareIncursionTrinkets = new OneOfLootItems(new LootItemInterface[0]);
    public static final RareIncursionTrinketsLootTable instance = new RareIncursionTrinketsLootTable();

    private RareIncursionTrinketsLootTable() {
        super(new LootItemInterface[]{rareIncursionTrinkets});
    }
}

