/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class BuildingPotionItem
extends SimplePotionItem {
    public BuildingPotionItem() {
        super(100, Item.Rarity.COMMON, "buildingpotion", 1800, "buildingpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
    }
}

