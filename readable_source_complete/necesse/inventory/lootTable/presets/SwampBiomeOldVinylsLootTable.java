/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class SwampBiomeOldVinylsLootTable
extends LootTable {
    public static OneOfLootItems oldVinyls = new OneOfLootItems(new LootItem("silverlakevinyl"), new LootItem("rialtovinyl"), new LootItem("awayvinyl"), new LootItem("lostgripvinyl"), new LootItem("konsoleglitchvinyl"));
    public static final SwampBiomeOldVinylsLootTable instance = new SwampBiomeOldVinylsLootTable();

    private SwampBiomeOldVinylsLootTable() {
        super(oldVinyls);
    }
}

