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
import necesse.inventory.item.armorItem.ChestArmorItem;

public class ArcanicChestplateArmorItem
extends ChestArmorItem {
    public ArcanicChestplateArmorItem() {
        super(29, 1900, Item.Rarity.EPIC, "arcanicchestplate", "arcanicarms", null);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 100), new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, Float.valueOf(1.5f)), new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.1f)));
    }
}

