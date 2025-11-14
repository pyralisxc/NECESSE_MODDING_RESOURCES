/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.nightsteel;

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
import necesse.inventory.lootTable.presets.IncursionArmorSetsLootTable;
import necesse.inventory.lootTable.presets.IncursionHeadArmorLootTable;
import necesse.level.maps.incursion.IncursionData;

public class NightsteelVeilArmorItem
extends SetHelmetArmorItem {
    public FloatUpgradeValue manaRegen = new FloatUpgradeValue().setBaseValue(1.5f).setUpgradedValue(1.0f, 1.5f);

    public NightsteelVeilArmorItem() {
        super(21, DamageTypeRegistry.MAGIC, 1900, IncursionHeadArmorLootTable.incursionHeadArmor, IncursionArmorSetsLootTable.incursionArmorSets, Item.Rarity.EPIC, "nightsteelveil", "nightsteelchestplate", "nightsteelboots", "nightsteelveilsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MAGIC_CRIT_CHANCE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.MAGIC_CRIT_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, this.manaRegen.getValue(this.getUpgradeTier(item))));
    }
}

