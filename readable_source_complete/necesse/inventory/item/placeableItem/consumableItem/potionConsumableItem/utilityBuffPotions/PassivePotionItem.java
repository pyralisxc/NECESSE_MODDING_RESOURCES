/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class PassivePotionItem
extends SimplePotionItem {
    public PassivePotionItem() {
        super(100, Item.Rarity.UNCOMMON, "passivepotion", 900, "passivepot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
    }
}

