/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.thief;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

public class ThiefsBootsArmorItem
extends BootsArmorItem {
    public ThiefsBootsArmorItem() {
        super(6, 650, "thiefsboots", FeetArmorLootTable.feetArmor);
        this.rarity = Item.Rarity.COMMON;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f)));
    }
}

