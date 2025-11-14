/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class SnowBiomeOldVinylsLootTable
extends LootTable {
    public static OneOfLootItems oldVinyls = new OneOfLootItems(new LootItem("homeatlastvinyl"), new LootItem("telltalevinyl"), new LootItem("icyrusevinyl"), new LootItem("icestarvinyl"), new LootItem("thecontrolroomvinyl"), new LootItem("milleniumvinyl"));
    public static final SnowBiomeOldVinylsLootTable instance = new SnowBiomeOldVinylsLootTable();

    private SnowBiomeOldVinylsLootTable() {
        super(oldVinyls);
    }
}

