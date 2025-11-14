/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.animalKeeper;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;
import necesse.inventory.lootTable.presets.CosmeticSetArmorLootTable;

public class AnimalKeeperHatArmorItem
extends SetHelmetArmorItem {
    public AnimalKeeperHatArmorItem() {
        super(0, null, 0, CosmeticArmorLootTable.cosmeticArmor, CosmeticSetArmorLootTable.cosmeticSetArmor, Item.Rarity.COMMON, "animalkeepershat", "animalkeepershirt", "animalkeepershoes", null);
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairMaskTextureName = "animalskeeperhat_hairmask";
    }
}

