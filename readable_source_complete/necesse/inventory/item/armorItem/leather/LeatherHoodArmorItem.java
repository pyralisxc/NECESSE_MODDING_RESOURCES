/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.leather;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class LeatherHoodArmorItem
extends SetHelmetArmorItem {
    public LeatherHoodArmorItem() {
        super(1, DamageTypeRegistry.RANGED, 100, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.NORMAL, "leatherhood", "leathershirt", "leatherboots", "leathersetbonus");
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.hairMaskTextureName = "snowhood_leatherhood_hairmask";
        this.canBeUsedForRaids = true;
    }
}

