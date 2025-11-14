/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class AccuracyPotionItem
extends SimplePotionItem {
    public AccuracyPotionItem() {
        super(100, Item.Rarity.COMMON, "accuracypotion", 300, "accuracypot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

