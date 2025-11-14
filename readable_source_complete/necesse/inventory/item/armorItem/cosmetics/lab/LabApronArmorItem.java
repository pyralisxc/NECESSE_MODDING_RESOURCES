/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.lab;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class LabApronArmorItem
extends ChestArmorItem {
    public LabApronArmorItem() {
        super(0, 0, Item.Rarity.COMMON, "labapron", "labapronarms", CosmeticArmorLootTable.cosmeticArmor);
    }
}

