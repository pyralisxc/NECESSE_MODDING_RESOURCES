/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class ChristmasPresentLootTable
extends LootTable {
    public static final OneOfLootItems potions = new OneOfLootItems(new LootItem("attackspeedpotion"), new LootItem("healthregenpotion"), new LootItem("manaregenpotion"), new LootItem("fishingpotion"), new LootItem("fireresistancepotion"), new LootItem("resistancepotion"), new LootItem("speedpotion"), new LootItem("battlepotion"), new LootItem("thornspotion"), new LootItem("accuracypotion"), new LootItem("knockbackpotion"), new LootItem("rapidpotion"));
    public static final OneOfTicketLootItems oneOfChristmasItems = new OneOfTicketLootItems(100, LootItem.between("snowball", 8, 16), 80, LootItem.between("candycane", 1, 2), 80, LootItem.between("cookies", 2, 4), 50, new LootItem("christmaswreath"), 40, new LootItem("snowmantrainingdummy"), 25, new LootItem("christmastree"), 25, new LootItem("theeldersjinglejamvinyl"), 15, new LootItem("snowlauncher"));
    public static final ChristmasPresentLootTable instance = new ChristmasPresentLootTable();

    private ChristmasPresentLootTable() {
        super(LootItem.offset("coin", 10, 5).splitItems(5), potions, oneOfChristmasItems);
    }
}

