/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class TempleChestLootTable
extends LootTable {
    public static final RotationLootItem mainItems = RotationLootItem.presetRotation(new LootItem("antiquebow"), new LootItem("antiquesword"), new LootItem("templependant"));
    public static final OneOfLootItems potions = new OneOfLootItems(LootItem.between("attackspeedpotion", 2, 4), LootItem.between("battlepotion", 2, 4), LootItem.between("resistancepotion", 2, 4), LootItem.between("thornspotion", 2, 4), LootItem.between("accuracypotion", 2, 4));
    public static final OneOfLootItems bars = new OneOfLootItems(LootItem.offset("goldbar", 5, 2), LootItem.offset("tungstenbar", 6, 2), LootItem.offset("ancientfossilbar", 6, 2));
    public static final LootTable extraItems = new LootTable(ChanceLootItem.offset(0.5f, "torch", 15, 5), new OneOfTicketLootItems(3, LootItem.between("ironbomb", 2, 5), 1, LootItem.between("dynamitestick", 1, 3)));
    public static final TempleChestLootTable instance = new TempleChestLootTable();

    public TempleChestLootTable() {
        super(mainItems, potions, bars, extraItems);
    }
}

