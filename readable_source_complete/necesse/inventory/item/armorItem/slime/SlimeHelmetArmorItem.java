/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.slime;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.IncursionArmorSetsLootTable;
import necesse.inventory.lootTable.presets.IncursionHeadArmorLootTable;
import necesse.level.maps.incursion.IncursionData;

public class SlimeHelmetArmorItem
extends SetHelmetArmorItem {
    public SlimeHelmetArmorItem() {
        super(28, DamageTypeRegistry.MELEE, 1900, IncursionHeadArmorLootTable.incursionHeadArmor, IncursionArmorSetsLootTable.incursionArmorSets, Item.Rarity.EPIC, "slimehelmet", "slimechestplate", "slimeboots", "slimehelmetsetbonus");
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MELEE_CRIT_CHANCE, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.MELEE_CRIT_DAMAGE, Float.valueOf(0.15f)));
    }
}

