/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class RuneboundCosmeticArmorLootTable
extends LootTable {
    public static final OneOfLootItems runeboundCosmeticArmor = new OneOfLootItems(new LootItem("runeboundcrown"), new LootItem("runeboundcrownmask"), new LootItem("runeboundskullhelmet"), new LootItem("runeboundhelmet"), new LootItem("runeboundhornhelmet"), new LootItem("runeboundhood"), new LootItem("runeboundbackbones"), new LootItem("runeboundrobe"), new LootItem("runeboundbonesrobe"), new LootItem("runeboundleatherchest"), new LootItem("runeboundboots"));
    public static final RuneboundCosmeticArmorLootTable instance = new RuneboundCosmeticArmorLootTable();

    private RuneboundCosmeticArmorLootTable() {
        super(runeboundCosmeticArmor);
    }
}

