/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.tungsten;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class TungstenBootsArmorItem
extends BootsArmorItem {
    public TungstenBootsArmorItem() {
        super(15, 1300, Item.Rarity.UNCOMMON, "tungstenboots", FeetArmorLootTable.feetArmor);
    }
}

