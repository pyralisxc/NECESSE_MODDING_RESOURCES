/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterAccuracyPotionItem
extends SimplePotionItem {
    public GreaterAccuracyPotionItem() {
        super(100, Item.Rarity.RARE, "greateraccuracypotion", 300, "greateraccuracypot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
        this.overridePotion("accuracypotion");
    }
}

