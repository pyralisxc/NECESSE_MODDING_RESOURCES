/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class RareIncursionArmorSetsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems rareIncursionArmorSetsRewards = new OneOfLootItems(new LootItemInterface[0]);
    public static final RareIncursionArmorSetsLootTable instance = new RareIncursionArmorSetsLootTable();

    private RareIncursionArmorSetsLootTable() {
        super(new LootItemInterface[]{rareIncursionArmorSetsRewards});
    }
}

