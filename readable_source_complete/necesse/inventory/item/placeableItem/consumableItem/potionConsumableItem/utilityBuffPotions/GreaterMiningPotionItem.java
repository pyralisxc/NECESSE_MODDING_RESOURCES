/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterMiningPotionItem
extends SimplePotionItem {
    public GreaterMiningPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "greaterminingpotion", 900, "greaterminingpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
        this.overridePotion("miningpotion");
    }
}

