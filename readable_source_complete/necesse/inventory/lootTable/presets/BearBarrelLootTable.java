/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;

public class BearBarrelLootTable
extends LootTable {
    public static final BearBarrelLootTable instance = new BearBarrelLootTable();

    private BearBarrelLootTable() {
        super(new LootItem("grizzlycub"), LootItem.offset("honey", 10, 5), LootItem.offset("roastedfish", 10, 5), ChanceLootItem.offset(0.5f, "torch", 15, 5));
    }
}

