/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class TreasurePotionItem
extends SimplePotionItem {
    public TreasurePotionItem() {
        super(100, Item.Rarity.UNCOMMON, "treasurepotion", 600, "treasurepot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "utilitypotions");
    }
}

