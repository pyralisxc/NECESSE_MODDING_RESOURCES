/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;

public class BowAndArrowsLootTable
extends LootTable {
    public static final BowAndArrowsLootTable instance = new BowAndArrowsLootTable();

    private BowAndArrowsLootTable() {
        super(new LootItem("copperbow"), LootItem.between("stonearrow", 32, 67), new ChanceLootItem(0.5f, "hunterhoodmask"));
    }
}

