/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionHeadArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionHeadArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionHeadArmorLootTable instance = new IncursionHeadArmorLootTable();

    private IncursionHeadArmorLootTable() {
        super(new LootItemInterface[]{incursionHeadArmor});
    }
}

