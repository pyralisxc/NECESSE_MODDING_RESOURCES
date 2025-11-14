/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class FishingPotionItem
extends SimplePotionItem {
    public FishingPotionItem() {
        super(100, Item.Rarity.COMMON, "fishingpotion", 300, "fishingpot1", "fishingpot2");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
    }
}

