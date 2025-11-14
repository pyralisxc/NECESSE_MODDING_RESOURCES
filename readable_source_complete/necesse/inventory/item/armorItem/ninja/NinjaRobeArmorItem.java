/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.ninja;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class NinjaRobeArmorItem
extends ChestArmorItem {
    public NinjaRobeArmorItem() {
        super(20, 1450, Item.Rarity.RARE, "ninjarobe", "ninjaarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.1f)));
    }
}

