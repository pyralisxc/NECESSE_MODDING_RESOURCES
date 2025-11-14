/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.frost;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class FrostHelmetArmorItem
extends SetHelmetArmorItem {
    public FrostHelmetArmorItem() {
        super(5, DamageTypeRegistry.MELEE, 500, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.COMMON, "frosthelmet", "frostchestplate", "frostboots", "frostsetbonus");
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MELEE_DAMAGE, Float.valueOf(0.1f)));
    }
}

