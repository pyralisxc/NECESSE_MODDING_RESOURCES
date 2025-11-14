/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.demonic;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class DemonicChestplateArmorItem
extends ChestArmorItem {
    public DemonicChestplateArmorItem() {
        super(7, 650, Item.Rarity.COMMON, "demonicchest", "demonicarms", BodyArmorLootTable.bodyArmor);
    }
}

