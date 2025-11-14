/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class AlchemistChestLootTable
extends LootTable {
    public static CountOfTicketLootItems potions = new CountOfTicketLootItems(3, 400, LootItem.between("healthpotion", 4, 6), LootItem.between("manapotion", 4, 6), LootItem.between("speedpotion", 1, 3), LootItem.between("healthregenpotion", 1, 3), LootItem.between("manaregenpotion", 1, 3), LootItem.between("attackspeedpotion", 1, 3), LootItem.between("fireresistancepotion", 1, 3), LootItem.between("fishingpotion", 1, 3), LootItem.between("battlepotion", 1, 3), LootItem.between("resistancepotion", 1, 3), LootItem.between("thornspotion", 1, 3), LootItem.between("accuracypotion", 1, 3), LootItem.between("minionpotion", 1, 3), LootItem.between("knockbackpotion", 1, 3), LootItem.between("rapidpotion", 1, 3), LootItem.between("spelunkerpotion", 1, 3), LootItem.between("miningpotion", 1, 3), LootItem.between("treasurepotion", 1, 3), LootItem.between("passivepotion", 1, 3), LootItem.between("buildingpotion", 1, 3));
    public static final AlchemistChestLootTable instance = new AlchemistChestLootTable();

    private AlchemistChestLootTable() {
        super(ChanceLootItem.offset(0.5f, "torch", 15, 5), potions, new ChanceLootItem(0.3f, "strikebanner"));
    }
}

