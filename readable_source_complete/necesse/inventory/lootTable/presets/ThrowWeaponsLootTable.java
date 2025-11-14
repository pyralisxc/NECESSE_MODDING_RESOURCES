/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class ThrowWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems throwWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final ThrowWeaponsLootTable instance = new ThrowWeaponsLootTable();

    private ThrowWeaponsLootTable() {
        super(new LootItemInterface[]{throwWeapons});
    }
}

