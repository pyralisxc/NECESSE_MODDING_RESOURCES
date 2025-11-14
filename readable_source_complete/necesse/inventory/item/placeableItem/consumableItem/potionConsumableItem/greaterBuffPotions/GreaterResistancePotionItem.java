/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterResistancePotionItem
extends SimplePotionItem {
    public GreaterResistancePotionItem() {
        super(100, Item.Rarity.RARE, "greaterresistancepotion", 300, "greaterresistpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
        this.overridePotion("resistancepotion");
    }
}

