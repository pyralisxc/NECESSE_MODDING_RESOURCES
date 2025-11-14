/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.armorItem.ArmorItem$FacialFeatureDrawMode
 */
package aphorea.items.armor.Gold;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;

public class GoldHat
extends AphSetHelmetArmorItem {
    public GoldHat() {
        super(1, DamageTypeRegistry.RANGED, 500, Item.Rarity.COMMON, "goldhat", "goldchestplate", "goldboots", "goldhatsetbonus");
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }
}

