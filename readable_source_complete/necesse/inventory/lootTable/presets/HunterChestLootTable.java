/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class HunterChestLootTable
extends LootTable {
    public static CountOfTicketLootItems lottery = new CountOfTicketLootItems(2, LootItem.between("beef", 5, 10), LootItem.between("rawmutton", 5, 10), LootItem.between("rabbitleg", 2, 8), LootItem.between("wool", 15, 30), LootItem.between("leather", 15, 30));
    public static final HunterChestLootTable instance = new HunterChestLootTable();

    private HunterChestLootTable() {
        super(ChanceLootItem.offset(0.5f, "torch", 25, 5), lottery, new ChanceLootItem(0.3f, "strikebanner"));
    }
}

