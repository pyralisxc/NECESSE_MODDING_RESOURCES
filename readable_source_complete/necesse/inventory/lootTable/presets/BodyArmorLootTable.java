/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class BodyArmorLootTable
extends OneOfLootItems {
    public static final OneOfLootItems bodyArmor = new OneOfLootItems(new LootItemInterface[0]);
    public static final BodyArmorLootTable instance = new BodyArmorLootTable();

    private BodyArmorLootTable() {
        super(new LootItemInterface[]{bodyArmor});
    }
}

