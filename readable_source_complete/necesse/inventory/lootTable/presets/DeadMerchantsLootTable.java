/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class DeadMerchantsLootTable
extends LootTable {
    public static CountOfTicketLootItems lottery = new CountOfTicketLootItems(1, new LootItem("binoculars"), new LootItem("boxingglovegun"), new LootItem("recipebook"), new LootItem("potionpouch"), new LootItem("brainonastick"), new LootItem("piratemap"), LootItem.between("rope", 2, 5));
    public static CountOfTicketLootItems randomFood = new CountOfTicketLootItems(1, LootItem.between("donut", 2, 6), LootItem.between("chickendrumstick", 2, 6), LootItem.between("roastedrabbitleg", 2, 6), LootItem.between("roastedmutton", 2, 6), LootItem.between("steak", 2, 6));
    public static final DeadMerchantsLootTable instance = new DeadMerchantsLootTable();

    private DeadMerchantsLootTable() {
        super(lottery, LootItem.offset("torch", 12, 4), LootItem.offset("coin", 80, 65), randomFood);
    }
}

