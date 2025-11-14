/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterHealthRegenPotionItem
extends SimplePotionItem {
    public GreaterHealthRegenPotionItem() {
        super(100, Item.Rarity.RARE, "greaterhealthregenpotion", 300, "greaterhealthregenpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
        this.overridePotion("healthregenpotion");
    }
}

