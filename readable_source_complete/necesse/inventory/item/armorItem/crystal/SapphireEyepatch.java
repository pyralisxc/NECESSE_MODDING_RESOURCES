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

public class SapphireEyepatch
extends SetHelmetArmorItem {
    public SapphireEyepatch() {
        super(23, null, 1900, IncursionHeadArmorLootTable.incursionHeadArmor, IncursionArmorSetsLootTable.incursionArmorSets, Item.Rarity.EPIC, "sapphireeyepatch", "crystalchestplate", "crystalboots", "sapphiresetbonus");
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_ATTACK_SPEED, Float.valueOf(0.25f)), new ModifierValue<Float>(BuffModifiers.PROJECTILE_VELOCITY, Float.valueOf(0.25f)), new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 10));
    }
}

