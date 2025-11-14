/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.leather;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class LeatherShirtArmorItem
extends ChestArmorItem {
    public LeatherShirtArmorItem() {
        super(2, 100, Item.Rarity.NORMAL, "leathershirt", "leatherarms", BodyArmorLootTable.bodyArmor);
    }
}

