/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.soldier;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class SoldierChestplateArmorItem
extends ChestArmorItem {
    public SoldierChestplateArmorItem() {
        super(6, 375, Item.Rarity.COMMON, "soldierchestplate", "soldierarms", BodyArmorLootTable.bodyArmor);
    }
}

