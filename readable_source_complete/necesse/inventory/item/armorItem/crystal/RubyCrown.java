/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.crystal;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.IncursionArmorSetsLootTable;
import necesse.inventory.lootTable.presets.IncursionHeadArmorLootTable;
import necesse.level.maps.incursion.IncursionData;

public class RubyCrown
extends SetHelmetArmorItem {
    public RubyCrown() {
        super(23, null, 1900, IncursionHeadArmorLootTable.incursionHeadArmor, IncursionArmorSetsLootTable.incursionArmorSets, Item.Rarity.EPIC, "rubycrown", "crystalchestplate", "crystalboots", "rubysetbonus");
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue<Float>(BuffModifiers.SUMMON_CRIT_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SUMMON_CRIT_CHANCE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SUMMONS_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SUMMON_ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SUMMONS_TARGET_RANGE, Float.valueOf(0.1f)));
    }
}

