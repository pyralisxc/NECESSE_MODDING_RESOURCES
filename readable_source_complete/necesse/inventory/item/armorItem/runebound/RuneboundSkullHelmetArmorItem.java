/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.runebound;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;
import necesse.inventory.lootTable.presets.CosmeticSetArmorLootTable;

public class RuneboundSkullHelmetArmorItem
extends SetHelmetArmorItem {
    public RuneboundSkullHelmetArmorItem() {
        super(0, null, 0, CosmeticArmorLootTable.cosmeticArmor, CosmeticSetArmorLootTable.cosmeticSetArmor, Item.Rarity.COMMON, "runeboundskullhelmet", "runeboundbonesrobe", "runeboundboots", null);
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.NO_FACIAL_FEATURE;
    }
}

