/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class WebPotionItem
extends SimplePotionItem {
    public WebPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "webpotion", 300, "webpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
    }
}

