/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class BowWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems bowWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final BowWeaponsLootTable instance = new BowWeaponsLootTable();

    private BowWeaponsLootTable() {
        super(new LootItemInterface[]{bowWeapons});
    }
}

