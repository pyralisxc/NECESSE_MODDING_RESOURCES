/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterManaRegenPotionItem
extends SimplePotionItem {
    public GreaterManaRegenPotionItem() {
        super(100, Item.Rarity.RARE, "greatermanaregenpotion", 300, "greatermanaregenpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
        this.overridePotion("manaregenpotion");
    }
}

