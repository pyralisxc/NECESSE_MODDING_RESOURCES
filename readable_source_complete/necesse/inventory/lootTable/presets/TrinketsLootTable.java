/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class TrinketsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems trinkets = new OneOfLootItems(new LootItemInterface[0]);
    public static final TrinketsLootTable instance = new TrinketsLootTable();

    private TrinketsLootTable() {
        super(new LootItemInterface[]{trinkets});
    }
}

