/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionBodyArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionBodyArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionBodyArmorLootTable instance = new IncursionBodyArmorLootTable();

    private IncursionBodyArmorLootTable() {
        super(new LootItemInterface[]{incursionBodyArmor});
    }
}

