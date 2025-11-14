/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterBuildingPotionItem
extends SimplePotionItem {
    public GreaterBuildingPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "greaterbuildingpotion", 1800, "greaterbuildingpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
        this.overridePotion("buildingpotion");
    }
}

