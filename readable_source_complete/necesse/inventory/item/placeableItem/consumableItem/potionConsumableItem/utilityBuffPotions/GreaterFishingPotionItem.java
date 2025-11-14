/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterFishingPotionItem
extends SimplePotionItem {
    public GreaterFishingPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "greaterfishingpotion", 300, "greaterfishingpot1", "greaterfishingpot2");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
        this.overridePotion("fishingpotion");
    }
}

