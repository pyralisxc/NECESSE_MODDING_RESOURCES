/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class MinionPotionItem
extends SimplePotionItem {
    public MinionPotionItem() {
        super(100, Item.Rarity.UNCOMMON, "minionpotion", 300, "minionpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
    }
}

