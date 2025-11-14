/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.pharaoh;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class PharaohsSandalsArmorItem
extends BootsArmorItem {
    public FloatUpgradeValue speed = new FloatUpgradeValue().setBaseValue(0.15f).setUpgradedValue(1.0f, 0.25f);

    public PharaohsSandalsArmorItem() {
        super(10, 1000, Item.Rarity.UNCOMMON, "pharaohssandals", FeetArmorLootTable.feetArmor);
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.UNDER_FACIAL_FEATURE;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(item))));
    }
}

