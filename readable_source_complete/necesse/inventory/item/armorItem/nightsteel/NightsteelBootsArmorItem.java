/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.nightsteel;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.IncursionFeetArmorLootTable;

public class NightsteelBootsArmorItem
extends BootsArmorItem {
    public FloatUpgradeValue speed = new FloatUpgradeValue().setBaseValue(0.25f).setUpgradedValue(1.0f, 0.25f);

    public NightsteelBootsArmorItem() {
        super(17, 1900, Item.Rarity.EPIC, "nightsteelboots", IncursionFeetArmorLootTable.incursionFeetArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(item))));
    }
}

