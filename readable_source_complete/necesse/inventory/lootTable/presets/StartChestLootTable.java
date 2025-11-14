/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class StartChestLootTable
extends LootTable {
    public static final StartChestLootTable instance = new StartChestLootTable();

    private StartChestLootTable() {
        super(new LootItem("settlementflag"), new LootItem("villagemap"), new LootItem("dungeonmap"), new LootItem("bread", 10), new LootItem("healthpotion", 5));
    }
}

