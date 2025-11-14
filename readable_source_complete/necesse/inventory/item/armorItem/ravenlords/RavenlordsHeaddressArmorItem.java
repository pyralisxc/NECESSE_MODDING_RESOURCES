/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.ravenlords;

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

public class RavenlordsHeaddressArmorItem
extends SetHelmetArmorItem {
    public RavenlordsHeaddressArmorItem() {
        super(23, null, 1900, IncursionHeadArmorLootTable.incursionHeadArmor, IncursionArmorSetsLootTable.incursionArmorSets, Item.Rarity.EPIC, "ravenlordsheaddress", "ravenlordschestplate", "ravenlordsboots", "ravenlordsheaddresssetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.defaultLootTier = 1.0f;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.3f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.1f)));
    }
}

