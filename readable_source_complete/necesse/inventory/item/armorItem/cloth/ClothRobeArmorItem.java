/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cloth;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class ClothRobeArmorItem
extends ChestArmorItem {
    public ClothRobeArmorItem() {
        super(2, 100, Item.Rarity.NORMAL, "clothrobe", "clothrobearms", BodyArmorLootTable.bodyArmor);
    }
}

