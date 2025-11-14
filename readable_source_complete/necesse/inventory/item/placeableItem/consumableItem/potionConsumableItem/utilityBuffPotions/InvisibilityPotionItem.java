/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class InvisibilityPotionItem
extends SimplePotionItem {
    public InvisibilityPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "invisibilitypotion", 600, "invispot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
    }
}

