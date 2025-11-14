/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class BattlePotionItem
extends SimplePotionItem {
    public BattlePotionItem() {
        super(100, Item.Rarity.COMMON, "battlepotion", 300, "battlepot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}

