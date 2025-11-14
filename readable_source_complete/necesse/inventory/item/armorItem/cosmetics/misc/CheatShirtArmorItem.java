/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.misc;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.cosmetics.misc.ShirtArmorItem;

public class CheatShirtArmorItem
extends ShirtArmorItem {
    public CheatShirtArmorItem() {
        super(1);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("Cheat Shirt");
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 1000), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(50.0f)), new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 1000), new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN_FLAT, Float.valueOf(10.0f)));
    }
}

