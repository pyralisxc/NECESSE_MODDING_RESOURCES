/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class DungeonChestLootTable
extends LootTable {
    public static final RotationLootItem mainItems = RotationLootItem.presetRotation(new LootItem("voidboomerang"), new LootItem("lightninghammer"), new LootItem("mobilitycloak"), new LootItem("mesmertablet"), new LootItem("voidspear"));
    public static final OneOfLootItems potions = new OneOfLootItems(LootItem.offset("healthpotion", 4, 2), LootItem.offset("manapotion", 4, 2), LootItem.offset("attackspeedpotion", 2, 1), LootItem.offset("healthregenpotion", 2, 1), LootItem.offset("manaregenpotion", 2, 1), LootItem.offset("speedpotion", 2, 1), LootItem.offset("battlepotion", 2, 1), LootItem.offset("thornspotion", 2, 1), LootItem.offset("accuracypotion", 2, 1), LootItem.offset("knockbackpotion", 2, 1), LootItem.offset("rapidpotion", 2, 1));
    public static final RotationLootItem vinyls = RotationLootItem.presetRotation(new LootItem("voidsembracevinyl"));
    public static final DungeonChestLootTable instance = new DungeonChestLootTable();

    private DungeonChestLootTable() {
        super(mainItems, potions, ChanceLootItem.between(0.5f, "torch", 10, 20), new ChanceLootItem(0.2f, "mysteriousportal"), new ChanceLootItemList(0.4f, new OneOfTicketLootItems(3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3))), new ChanceLootItemList(0.25f, new OneOfTicketLootItems(3, LootItem.between("recallscroll", 1, 2), 1, new LootItem("teleportationscroll"))), new ChanceLootItem(0.33f, "enchantingscroll"), new ChanceLootItemList(0.2f, LootTablePresets.dungeonBiomeOldVinylsLootTable), new ChanceLootItemList(0.25f, vinyls));
    }
}

