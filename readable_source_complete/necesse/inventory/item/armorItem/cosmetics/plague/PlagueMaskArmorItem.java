/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.plague;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;
import necesse.inventory.lootTable.presets.CosmeticSetArmorLootTable;

public class PlagueMaskArmorItem
extends SetHelmetArmorItem {
    public PlagueMaskArmorItem() {
        super(0, null, 0, CosmeticArmorLootTable.cosmeticArmor, CosmeticSetArmorLootTable.cosmeticSetArmor, Item.Rarity.UNCOMMON, "plaguemask", "plaguerobe", "plagueboots", null);
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
    }
}

