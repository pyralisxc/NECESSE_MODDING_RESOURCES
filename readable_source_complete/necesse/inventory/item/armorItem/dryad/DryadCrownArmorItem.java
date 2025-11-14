/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.dryad;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class DryadCrownArmorItem
extends SetHelmetArmorItem {
    public DryadCrownArmorItem() {
        super(6, DamageTypeRegistry.SUMMON, 1550, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "dryadcrown", "dryadchestplate", "dryadboots", "dryadcrownsetbonus");
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SUMMON_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.CRIT_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Integer>(BuffModifiers.MAX_SUMMONS, 1));
    }
}

