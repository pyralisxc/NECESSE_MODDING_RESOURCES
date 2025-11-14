/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.leather;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class LeatherBootsArmorItem
extends BootsArmorItem {
    public LeatherBootsArmorItem() {
        super(1, 100, Item.Rarity.NORMAL, "leatherboots", FeetArmorLootTable.feetArmor);
    }
}

