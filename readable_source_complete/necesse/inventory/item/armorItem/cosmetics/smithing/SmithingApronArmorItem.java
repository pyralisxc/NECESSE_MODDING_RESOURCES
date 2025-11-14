/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.smithing;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class SmithingApronArmorItem
extends ChestArmorItem {
    public SmithingApronArmorItem() {
        super(0, 0, Item.Rarity.COMMON, "smithingapron", "smithingapronarms", CosmeticArmorLootTable.cosmeticArmor);
    }
}

