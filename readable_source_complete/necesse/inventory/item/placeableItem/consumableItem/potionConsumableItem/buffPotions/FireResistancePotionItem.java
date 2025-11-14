/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class FireResistancePotionItem
extends SimplePotionItem {
    public FireResistancePotionItem() {
        super(100, Item.Rarity.COMMON, "fireresistancepotion", 300, "fireresistpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

