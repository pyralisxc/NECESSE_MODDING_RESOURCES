/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionFeetArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionFeetArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionFeetArmorLootTable instance = new IncursionFeetArmorLootTable();

    private IncursionFeetArmorLootTable() {
        super(new LootItemInterface[]{incursionFeetArmor});
    }
}

