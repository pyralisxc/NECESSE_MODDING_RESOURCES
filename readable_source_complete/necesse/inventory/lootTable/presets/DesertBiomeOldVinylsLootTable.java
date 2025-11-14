/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class DesertBiomeOldVinylsLootTable
extends LootTable {
    public static OneOfLootItems oldVinyls = new OneOfLootItems(new LootItem("eyesofthedesertvinyl"), new LootItem("bythefieldvinyl"), new LootItem("sunstonesvinyl"), new LootItem("caravantusksvinyl"), new LootItem("beatdownvinyl"), new LootItem("kandiruvinyl"));
    public static final DesertBiomeOldVinylsLootTable instance = new DesertBiomeOldVinylsLootTable();

    private DesertBiomeOldVinylsLootTable() {
        super(oldVinyls);
    }
}

