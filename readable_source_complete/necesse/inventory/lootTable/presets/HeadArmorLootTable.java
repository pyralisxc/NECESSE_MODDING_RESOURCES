/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class HeadArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems headArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final HeadArmorLootTable instance = new HeadArmorLootTable();

    private HeadArmorLootTable() {
        super(new LootItemInterface[]{headArmor});
    }
}

