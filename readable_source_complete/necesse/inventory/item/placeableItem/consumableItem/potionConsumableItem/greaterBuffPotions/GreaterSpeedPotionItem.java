/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterSpeedPotionItem
extends SimplePotionItem {
    public GreaterSpeedPotionItem() {
        super(100, Item.Rarity.RARE, "greaterspeedpotion", 300, "greaterspeedpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
        this.overridePotion("speedpotion");
    }
}

