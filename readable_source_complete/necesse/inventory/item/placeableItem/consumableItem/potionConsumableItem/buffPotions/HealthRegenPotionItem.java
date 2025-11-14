/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class HealthRegenPotionItem
extends SimplePotionItem {
    public HealthRegenPotionItem() {
        super(100, Item.Rarity.COMMON, "healthregenpotion", 300, "healthregenpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

