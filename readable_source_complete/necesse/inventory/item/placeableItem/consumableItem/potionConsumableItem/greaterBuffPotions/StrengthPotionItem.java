/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class StrengthPotionItem
extends SimplePotionItem {
    public StrengthPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "strengthpotion", 300, "strengthpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
    }
}

