/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class ArmorSetsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems armorSets = new OneOfLootItems(new LootItemInterface[0]);
    public static final ArmorSetsLootTable instance = new ArmorSetsLootTable();

    private ArmorSetsLootTable() {
        super(new LootItemInterface[]{armorSets});
    }
}

