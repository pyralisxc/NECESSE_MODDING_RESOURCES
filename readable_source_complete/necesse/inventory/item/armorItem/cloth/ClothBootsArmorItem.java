/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cloth;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class ClothBootsArmorItem
extends BootsArmorItem {
    public ClothBootsArmorItem() {
        super(1, 100, Item.Rarity.NORMAL, "clothboots", FeetArmorLootTable.feetArmor);
    }
}

