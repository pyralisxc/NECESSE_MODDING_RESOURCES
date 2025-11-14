/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.sharpshooter;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class SharpshooterCoatArmorItem
extends ChestArmorItem {
    public SharpshooterCoatArmorItem() {
        super(30, 1750, Item.Rarity.UNCOMMON, "sharpshootercoat", "sharpshooterarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_ATTACK_SPEED, Float.valueOf(0.1f)));
    }
}

