/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.bloodplate;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class BloodplateBootsArmorItem
extends BootsArmorItem {
    public FloatUpgradeValue speed = new FloatUpgradeValue().setBaseValue(0.1f).setUpgradedValue(1.0f, 0.25f);
    public FloatUpgradeValue healthRegen = new FloatUpgradeValue().setBaseValue(0.15f).setUpgradedValue(1.0f, 0.5f);

    public BloodplateBootsArmorItem() {
        super(5, 400, Item.Rarity.UNCOMMON, "bloodplateboots", FeetArmorLootTable.feetArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(item))), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, this.healthRegen.getValue(this.getUpgradeTier(item))));
    }
}

