/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class ManaRegenPotionItem
extends SimplePotionItem {
    public ManaRegenPotionItem() {
        super(100, Item.Rarity.COMMON, "manaregenpotion", 300, "manaregenpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

