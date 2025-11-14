/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionArmorSetsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionArmorSets = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionArmorSetsLootTable instance = new IncursionArmorSetsLootTable();

    private IncursionArmorSetsLootTable() {
        super(new LootItemInterface[]{incursionArmorSets});
    }
}

