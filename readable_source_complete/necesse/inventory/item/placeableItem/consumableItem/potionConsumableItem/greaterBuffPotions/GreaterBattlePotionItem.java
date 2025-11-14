/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public class GreaterBattlePotionItem
extends SimplePotionItem {
    public GreaterBattlePotionItem() {
        super(100, Item.Rarity.RARE, "greaterbattlepotion", 300, "greaterbattlepot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "greaterbuffpotions");
        this.overridePotion("battlepotion");
    }
}

