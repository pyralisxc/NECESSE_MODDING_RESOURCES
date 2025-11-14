/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.misc;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.HelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class ChristmasHatArmorItem
extends HelmetArmorItem {
    public ChristmasHatArmorItem() {
        super(0, null, 0, Item.Rarity.UNCOMMON, "christmashat", CosmeticArmorLootTable.cosmeticArmor);
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.hairMaskTextureName = "christmashat_hairmask";
    }
}

