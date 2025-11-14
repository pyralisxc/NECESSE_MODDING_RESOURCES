/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.armorItem.ArmorModifiers
 */
package aphorea.items.armor.Infected;

import aphorea.items.vanillaitemtypes.armor.AphBootsArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class InfectedBoots
extends AphBootsArmorItem {
    public InfectedBoots() {
        super(12, 1300, Item.Rarity.UNCOMMON, "infectedboots");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(0.1f)), new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(0.1f))});
    }
}

