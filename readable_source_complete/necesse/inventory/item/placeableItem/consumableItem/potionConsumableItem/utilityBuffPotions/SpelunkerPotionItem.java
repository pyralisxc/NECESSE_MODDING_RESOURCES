/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class SpelunkerPotionItem
extends SimplePotionItem {
    public SpelunkerPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "spelunkerpotion", 600, "spelunkerpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
    }
}

