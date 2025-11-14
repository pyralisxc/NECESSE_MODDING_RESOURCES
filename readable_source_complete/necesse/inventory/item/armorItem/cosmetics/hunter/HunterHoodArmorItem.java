/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.hunter;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;
import necesse.inventory.lootTable.presets.CosmeticSetArmorLootTable;

public class HunterHoodArmorItem
extends SetHelmetArmorItem {
    public HunterHoodArmorItem() {
        super(0, null, 0, CosmeticArmorLootTable.cosmeticArmor, CosmeticSetArmorLootTable.cosmeticSetArmor, Item.Rarity.COMMON, "hunterhood", "huntershirt", "hunterboots", null);
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.hairMaskTextureName = "turban_sailorhat_hunterhood_hairmask";
    }
}

