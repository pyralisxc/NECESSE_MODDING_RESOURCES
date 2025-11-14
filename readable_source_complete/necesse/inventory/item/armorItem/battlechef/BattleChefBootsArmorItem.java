/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.battlechef;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.IncursionFeetArmorLootTable;

public class BattleChefBootsArmorItem
extends BootsArmorItem {
    public FloatUpgradeValue speed = new FloatUpgradeValue().setBaseValue(0.05f).setUpgradedValue(1.0f, 0.1f).setUpgradedValue(5.0f, 0.15f);

    public BattleChefBootsArmorItem() {
        super(16, 2000, Item.Rarity.EPIC, "battlechefboots", IncursionFeetArmorLootTable.incursionFeetArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(item))));
    }
}

