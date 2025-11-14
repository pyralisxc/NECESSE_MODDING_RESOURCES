/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class AbandonedMineChestLootTable
extends LootTable {
    public static final OneOfLootItems potions = new OneOfLootItems(LootItem.offset("attackspeedpotion", 3, 1), LootItem.offset("battlepotion", 3, 1), LootItem.offset("resistancepotion", 3, 1), LootItem.offset("thornspotion", 3, 1), LootItem.offset("accuracypotion", 3, 1), LootItem.between("recallscroll", 1, 2));
    public static final OneOfLootItems bars = new OneOfLootItems(LootItem.offset("ironbar", 5, 1), LootItem.offset("goldbar", 3, 1), LootItem.offset("tungstenbar", 4, 1));
    public static final LootTable extraItems = new LootTable(ChanceLootItem.offset(0.5f, "torch", 15, 5), new OneOfTicketLootItems(3, LootItem.between("ironbomb", 4, 8), 1, LootItem.between("dynamitestick", 3, 5)), new ChanceLootItem(0.4f, "shadowgate"));
    public static final AbandonedMineChestLootTable instance = new AbandonedMineChestLootTable();

    public AbandonedMineChestLootTable() {
        super(potions, bars, extraItems);
    }
}

