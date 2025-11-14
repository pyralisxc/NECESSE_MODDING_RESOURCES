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
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class BloodplateChestplateArmorItem
extends ChestArmorItem {
    public FloatUpgradeValue healthRegen = new FloatUpgradeValue().setBaseValue(0.3f).setUpgradedValue(1.0f, 1.0f);

    public BloodplateChestplateArmorItem() {
        super(8, 400, Item.Rarity.UNCOMMON, "bloodplatechestplate", "bloodplatearms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, this.healthRegen.getValue(this.getUpgradeTier(item))));
    }
}

