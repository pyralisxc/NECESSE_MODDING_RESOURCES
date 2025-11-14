/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class KnockbackPotionItem
extends SimplePotionItem {
    public KnockbackPotionItem() {
        super(100, Item.Rarity.COMMON, "knockbackpotion", 300, "knockbackpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

