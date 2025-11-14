/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class StartDisplayStandLootTable
extends LootTable {
    public static final StartDisplayStandLootTable instance = new StartDisplayStandLootTable();

    private StartDisplayStandLootTable() {
        super(new LootItem("leatherdashers"));
    }
}

