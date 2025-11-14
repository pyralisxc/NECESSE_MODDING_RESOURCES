/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemCategory
 */
package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphSimplePotionItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class VenomExtract
extends AphSimplePotionItem {
    public VenomExtract() {
        super(100, Item.Rarity.COMMON, "venomextractbuff", 300, "venomextractbuff");
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"consumable", "buffpotions"});
    }
}

