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
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class NinjaHoodArmorItem
extends SetHelmetArmorItem {
    public NinjaHoodArmorItem() {
        super(16, null, 1450, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.RARE, "ninjahood", "ninjarobe", "ninjashoes", "ninjasetbonus");
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.CRIT_DAMAGE, Float.valueOf(0.25f)));
    }
}

