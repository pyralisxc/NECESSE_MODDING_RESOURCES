/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cryowitch;

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

public class CryoWitchHatArmorItem
extends SetHelmetArmorItem {
    public FloatUpgradeValue manaRegen = new FloatUpgradeValue().setBaseValue(1.0f).setUpgradedValue(1.0f, 1.5f);

    public CryoWitchHatArmorItem() {
        super(16, DamageTypeRegistry.MAGIC, 1450, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "cryowitchhat", "cryowitchrobe", "cryowitchshoes", "cryowitchsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue<Float>(BuffModifiers.SUMMON_ATTACK_SPEED, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, this.manaRegen.getValue(this.getUpgradeTier(item))), new ModifierValue<Float>(BuffModifiers.MAGIC_ATTACK_SPEED, Float.valueOf(0.15f)));
    }
}

