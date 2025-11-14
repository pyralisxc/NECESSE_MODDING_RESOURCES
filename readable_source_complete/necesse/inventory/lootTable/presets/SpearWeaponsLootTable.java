/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class SpearWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems spearWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final SpearWeaponsLootTable instance = new SpearWeaponsLootTable();

    private SpearWeaponsLootTable() {
        super(new LootItemInterface[]{spearWeapons});
    }
}

