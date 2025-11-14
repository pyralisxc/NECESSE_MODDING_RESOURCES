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
 *  necesse.inventory.item.armorItem.ArmorItem$FacialFeatureDrawMode
 *  necesse.inventory.item.armorItem.ArmorItem$HairDrawMode
 *  necesse.inventory.item.armorItem.ArmorModifiers
 */
package aphorea.items.armor.Swamp;

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

public class SwampMask
extends AphSetHelmetArmorItem {
    public SwampMask() {
        super(4, DamageTypeRegistry.MELEE, 400, Item.Rarity.COMMON, "swampmask", "swampchestplate", "swampboots", "swampmasksetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(0.05f)), new ModifierValue(BuffModifiers.MAX_RESILIENCE_FLAT, (Object)3)});
    }
}

