/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterRapidPotionItem
extends SimplePotionItem {
    public GreaterRapidPotionItem() {
        super(100, Item.Rarity.RARE, "greaterrapidpotion", 300, "greaterrapidpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
        this.overridePotion("rapidpotion");
    }
}

