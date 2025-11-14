/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.presets.IncursionLootLists;

public class ArcanicChestLootTable
extends LootTable {
    public static final ArcanicChestLootTable instance = new ArcanicChestLootTable();
    public OneOfTicketLootItems oneOfItems = new OneOfTicketLootItems(35, LootItem.between("greatermanapotion", 1, 2), 20, LootItem.between("greaterhealthpotion", 1, 2), 35, IncursionLootLists.greaterPotions, 25, IncursionLootLists.tierOneEssences, 10, IncursionLootLists.tierTwoEssences, 3, IncursionLootLists.arcanicArmorAndWeapons);

    private ArcanicChestLootTable() {
        super(LootItem.offset("altardust", 25, 12), LootItem.offset("electrifiedmana", 7, 2), ChanceLootItem.between(0.05f, "omnicrystal", 2, 4));
        this.items.add(this.oneOfItems);
    }
}

