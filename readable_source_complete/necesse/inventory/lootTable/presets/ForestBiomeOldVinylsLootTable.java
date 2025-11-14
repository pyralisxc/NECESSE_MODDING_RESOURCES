/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class ForestBiomeOldVinylsLootTable
extends LootTable {
    public static OneOfLootItems oldVinyls = new OneOfLootItems(new LootItem("homevinyl"), new LootItem("waterfaevinyl"), new LootItem("musesvinyl"), new LootItem("runningvinyl"), new LootItem("grindthealarmsvinyl"), new LootItem("elektrakvinyl"), new LootItem("halodromevinyl"));
    public static final ForestBiomeOldVinylsLootTable instance = new ForestBiomeOldVinylsLootTable();

    private ForestBiomeOldVinylsLootTable() {
        super(oldVinyls);
    }
}

