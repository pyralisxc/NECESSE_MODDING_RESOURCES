/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class FeetArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems feetArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final FeetArmorLootTable instance = new FeetArmorLootTable();

    private FeetArmorLootTable() {
        super(new LootItemInterface[]{feetArmor});
    }
}

