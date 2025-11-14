/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class RangerPotionItem
extends SimplePotionItem {
    public RangerPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "rangerpotion", 300, "rangerpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
    }
}

