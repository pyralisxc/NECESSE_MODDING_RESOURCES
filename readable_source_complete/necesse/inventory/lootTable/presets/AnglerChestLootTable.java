/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;

public class AnglerChestLootTable
extends LootTable {
    public static final AnglerChestLootTable instance = new AnglerChestLootTable();

    private AnglerChestLootTable() {
        super(LootItem.offset("wormbait", 20, 5), LootItem.offset("anglersbait", 10, 5), ChanceLootItem.offset(0.5f, "torch", 15, 5), new ChanceLootItem(0.3f, "strikebanner"));
    }
}

