/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.merchant;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class MerchantBootsArmorItem
extends BootsArmorItem {
    public MerchantBootsArmorItem() {
        super(0, 0, Item.Rarity.COMMON, "merchantboots", CosmeticArmorLootTable.cosmeticArmor);
    }
}

