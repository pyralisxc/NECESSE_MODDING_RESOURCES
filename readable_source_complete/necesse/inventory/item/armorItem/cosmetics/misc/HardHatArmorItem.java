/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.misc;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;
import necesse.inventory.lootTable.presets.CosmeticSetArmorLootTable;

public class HardHatArmorItem
extends SetHelmetArmorItem {
    public HardHatArmorItem() {
        super(0, null, 0, CosmeticArmorLootTable.cosmeticArmor, CosmeticSetArmorLootTable.cosmeticSetArmor, Item.Rarity.COMMON, "hardhat", "smithingapron", "smithingshoes", null);
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.hairMaskTextureName = "safarihat_hardhat_minerhat_hairmask";
    }
}

