/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cloth;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class ClothHatArmorItem
extends SetHelmetArmorItem {
    public FloatUpgradeValue manaRegen = new FloatUpgradeValue().setBaseValue(0.5f).setUpgradedValue(1.0f, 1.5f);

    public ClothHatArmorItem() {
        super(1, DamageTypeRegistry.MAGIC, 100, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.NORMAL, "clothhat", "clothrobe", "clothboots", "clothrobesetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairMaskTextureName = "clothhat_hairmask";
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, this.manaRegen.getValue(this.getUpgradeTier(item))));
    }
}

