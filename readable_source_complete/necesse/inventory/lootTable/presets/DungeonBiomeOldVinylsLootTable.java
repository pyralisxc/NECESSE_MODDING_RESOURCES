/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class DungeonBiomeOldVinylsLootTable
extends LootTable {
    public static OneOfLootItems oldVinyls = new OneOfLootItems(new LootItem("kronosvinyl"), new LootItem("airlockfailurevinyl"), new LootItem("siegevinyl"));
    public static final DungeonBiomeOldVinylsLootTable instance = new DungeonBiomeOldVinylsLootTable();

    private DungeonBiomeOldVinylsLootTable() {
        super(oldVinyls);
    }
}

