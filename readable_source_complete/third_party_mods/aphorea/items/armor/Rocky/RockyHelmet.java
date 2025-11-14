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
package aphorea.items.armor.Rocky;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class RockyHelmet
extends AphSetHelmetArmorItem {
    public RockyHelmet() {
        super(7, DamageTypeRegistry.MELEE, 250, Item.Rarity.COMMON, "rockyhelmet", "rockychestplate", "rockyboots", "rockysetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(-0.07f))});
    }
}

