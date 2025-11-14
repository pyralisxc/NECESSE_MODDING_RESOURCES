/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class CarpenterChestLootTable
extends LootTable {
    public static CountOfTicketLootItems lottery = new CountOfTicketLootItems(3, LootItem.between("oaklog", 25, 40), LootItem.between("palmlog", 25, 40), LootItem.between("pinelog", 25, 40), LootItem.between("sprucelog", 25, 40), LootItem.between("willowlog", 25, 40), LootItem.between("stone", 60, 100), LootItem.between("swampstone", 60, 100), LootItem.between("sandstone", 60, 100), LootItem.between("snowstone", 60, 100));
    public static final CarpenterChestLootTable instance = new CarpenterChestLootTable();

    private CarpenterChestLootTable() {
        super(LootItem.offset("torch", 25, 5), lottery, new ChanceLootItem(0.3f, "strikebanner"));
    }
}

