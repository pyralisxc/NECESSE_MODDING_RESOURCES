/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.CrateLootTable;

public class RuneboundTombLootTable
extends LootTable {
    public static final RuneboundTombLootTable instance = new RuneboundTombLootTable();

    private RuneboundTombLootTable() {
        super(new ChanceLootItem(0.15f, "enchantingscroll"), CrateLootTable.basicCrate, CrateLootTable.basicCrate, LootItem.offset("coin", 35, 5), ChanceLootItem.between(0.3f, "amber", 1, 2), ChanceLootItem.between(0.5f, "dryadsapling", 3, 5), ChanceLootItem.between(0.7f, "bone", 3, 5));
    }
}

