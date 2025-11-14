/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class FarmerChestLootTable
extends LootTable {
    public static CountOfTicketLootItems crops = new CountOfTicketLootItems(2, new LootItemList(LootItem.offset("sunflowerseed", 15, 4), LootItem.offset("sunflower", 10, 2)), new LootItemList(LootItem.offset("firemoneseed", 15, 4), LootItem.offset("firemone", 10, 2)), new LootItemList(LootItem.offset("iceblossomseed", 15, 4), LootItem.offset("iceblossom", 10, 2)), new LootItemList(LootItem.offset("wheatseed", 15, 4), LootItem.offset("wheat", 10, 2)), LootItem.offset("mushroom", 15, 4), new LootItemList(LootItem.offset("cornseed", 15, 4), LootItem.offset("corn", 10, 2)), new LootItemList(LootItem.offset("tomatoseed", 15, 4), LootItem.offset("tomato", 10, 2)), new LootItemList(LootItem.offset("chilipepperseed", 15, 4), LootItem.offset("chilipepper", 10, 2)), new LootItemList(LootItem.offset("sugarbeetseed", 15, 4), LootItem.offset("sugarbeet", 10, 2)));
    public static final FarmerChestLootTable instance = new FarmerChestLootTable();

    private FarmerChestLootTable() {
        super(LootItem.offset("fertilizer", 20, 5), ChanceLootItem.offset(0.5f, "torch", 15, 5), crops, new ChanceLootItem(0.3f, "strikebanner"));
    }
}

