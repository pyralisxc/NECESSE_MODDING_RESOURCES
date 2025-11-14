/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.armorItem.ArmorItem$HairDrawMode
 *  necesse.inventory.item.armorItem.ArmorModifiers
 */
package aphorea.items.armor.Spinel;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SpinelHelmet
extends AphSetHelmetArmorItem {
    public SpinelHelmet() {
        super(24, DamageTypeRegistry.MELEE, 1300, Item.Rarity.UNCOMMON, "spinelhelmet", "spinelchestplate", "spinelboots", "spinelhelmetsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(0.3f)), new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(-0.05f)), new ModifierValue(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf(-0.1f)), new ModifierValue(BuffModifiers.BLINDNESS, (Object)Float.valueOf(0.4f))});
    }
}

