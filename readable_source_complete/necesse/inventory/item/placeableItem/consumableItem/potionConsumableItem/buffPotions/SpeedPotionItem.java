/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class SpeedPotionItem
extends SimplePotionItem {
    public SpeedPotionItem() {
        super(100, Item.Rarity.COMMON, "speedpotion", 300, "speedpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

