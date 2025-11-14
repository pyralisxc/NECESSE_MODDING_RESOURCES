/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.tungsten;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class TungstenChestplateArmorItem
extends ChestArmorItem {
    public TungstenChestplateArmorItem() {
        super(25, 1300, Item.Rarity.UNCOMMON, "tungstenchest", "tungstenarms", BodyArmorLootTable.bodyArmor);
    }
}

