/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.hunter;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class HunterShirtArmorItem
extends ChestArmorItem {
    public HunterShirtArmorItem() {
        super(0, 0, Item.Rarity.COMMON, "huntershirt", "huntershirtarms", CosmeticArmorLootTable.cosmeticArmor);
    }
}

