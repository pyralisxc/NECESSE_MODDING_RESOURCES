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

public class FrostHoodArmorItem
extends SetHelmetArmorItem {
    public FrostHoodArmorItem() {
        super(4, DamageTypeRegistry.RANGED, 500, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.COMMON, "frosthood", "frostchestplate", "frostboots", "frosthoodsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.hairMaskTextureName = "frosthood_hairmask";
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_DAMAGE, Float.valueOf(0.1f)));
    }
}

