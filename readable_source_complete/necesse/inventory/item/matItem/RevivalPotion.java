/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.matItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.matItem.MatItem;

public class RevivalPotion
extends MatItem {
    public RevivalPotion() {
        super(250, Item.Rarity.RARE, "revivalpot");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "resourcepotions");
    }
}

