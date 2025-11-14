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
package aphorea.items.armor.Swamp;

import aphorea.items.vanillaitemtypes.armor.AphBootsArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SwampBoots
extends AphBootsArmorItem {
    public SwampBoots() {
        super(3, 400, Item.Rarity.COMMON, "swampboots");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING, (Object)Float.valueOf(0.05f)), new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(0.1f))});
    }
}

