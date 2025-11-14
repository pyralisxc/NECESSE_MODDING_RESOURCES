/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class CosmeticArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems cosmeticArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final CosmeticArmorLootTable instance = new CosmeticArmorLootTable();

    private CosmeticArmorLootTable() {
        super(new LootItemInterface[]{cosmeticArmor});
    }
}

