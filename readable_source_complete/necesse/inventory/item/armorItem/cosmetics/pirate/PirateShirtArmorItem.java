/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.pirate;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class PirateShirtArmorItem
extends ChestArmorItem {
    public PirateShirtArmorItem() {
        super(0, 0, Item.Rarity.UNCOMMON, "pirateshirt", "piratearms", CosmeticArmorLootTable.cosmeticArmor);
    }
}

