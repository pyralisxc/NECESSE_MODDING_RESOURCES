/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class WisdomPotionItem
extends SimplePotionItem {
    public WisdomPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "wisdompotion", 300, "wisdompot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
    }
}

