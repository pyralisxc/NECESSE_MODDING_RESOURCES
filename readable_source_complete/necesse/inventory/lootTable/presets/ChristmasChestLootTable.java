/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class ChristmasChestLootTable
extends LootTable {
    public static final ChristmasChestLootTable instance = new ChristmasChestLootTable();

    private ChristmasChestLootTable() {
        super(LootItem.between("greenwrappingpaper", 2, 8), LootItem.between("bluewrappingpaper", 2, 8), LootItem.between("redwrappingpaper", 2, 8), LootItem.between("yellowwrappingpaper", 2, 8), LootItem.between("candycane", 1, 5), LootItem.between("cookies", 1, 5), new LootItem("christmashat"));
    }
}

