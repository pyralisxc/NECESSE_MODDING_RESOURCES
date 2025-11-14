/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.soldier;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class SoldierBootsArmorItem
extends BootsArmorItem {
    public SoldierBootsArmorItem() {
        super(3, 375, Item.Rarity.COMMON, "soldierboots", FeetArmorLootTable.feetArmor);
    }
}

