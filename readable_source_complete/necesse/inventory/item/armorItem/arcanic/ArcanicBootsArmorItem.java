/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.arcanic;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class ArcanicBootsArmorItem
extends BootsArmorItem {
    public FloatUpgradeValue speed = new FloatUpgradeValue().setBaseValue(0.25f).setUpgradedValue(1.0f, 0.25f);

    public ArcanicBootsArmorItem() {
        super(17, 1900, Item.Rarity.EPIC, "arcanicboots", null);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(item))), new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 100), new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, Float.valueOf(1.5f)));
    }
}

