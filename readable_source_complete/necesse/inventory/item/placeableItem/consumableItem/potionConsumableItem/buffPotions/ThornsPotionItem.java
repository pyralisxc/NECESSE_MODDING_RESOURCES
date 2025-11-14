/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class ThornsPotionItem
extends SimplePotionItem {
    public ThornsPotionItem() {
        super(100, Item.Rarity.COMMON, "thornspotion", 300, "thornspot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

