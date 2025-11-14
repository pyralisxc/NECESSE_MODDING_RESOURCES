/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.presets.IncursionLootLists;

public class RavenChestLootTable
extends LootTable {
    public static final RavenChestLootTable instance = new RavenChestLootTable();
    public OneOfTicketLootItems oneOfItems = new OneOfTicketLootItems(35, LootItem.between("greaterhealthpotion", 1, 2), 20, LootItem.between("greatermanapotion", 1, 2), 35, IncursionLootLists.greaterPotions, 25, IncursionLootLists.tierOneEssences, 10, IncursionLootLists.tierTwoEssences, 3, IncursionLootLists.ravenArmorAndWeapons);

    private RavenChestLootTable() {
        super(LootItem.offset("altardust", 25, 12), LootItem.offset("ravenfeather", 7, 2), new ChanceLootItem(0.1f, "egg", 1));
        this.items.add(this.oneOfItems);
    }
}

