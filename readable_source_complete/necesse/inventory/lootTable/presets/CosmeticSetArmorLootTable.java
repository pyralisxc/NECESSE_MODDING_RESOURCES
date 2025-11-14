/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class CosmeticSetArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems cosmeticSetArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final CosmeticSetArmorLootTable instance = new CosmeticSetArmorLootTable();

    private CosmeticSetArmorLootTable() {
        super(new LootItemInterface[]{cosmeticSetArmor});
    }
}

