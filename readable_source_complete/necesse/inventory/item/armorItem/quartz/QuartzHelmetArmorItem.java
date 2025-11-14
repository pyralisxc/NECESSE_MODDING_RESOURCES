/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.quartz;

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

public class QuartzHelmetArmorItem
extends SetHelmetArmorItem {
    public QuartzHelmetArmorItem() {
        super(18, DamageTypeRegistry.MELEE, 1000, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "quartzhelmet", "quartzchestplate", "quartzboots", "quartzhelmetsetbonus");
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.05f)));
    }
}

