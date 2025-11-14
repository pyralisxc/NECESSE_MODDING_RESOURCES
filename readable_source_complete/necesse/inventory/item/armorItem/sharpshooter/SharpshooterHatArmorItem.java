/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.sharpshooter;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class SharpshooterHatArmorItem
extends SetHelmetArmorItem {
    public SharpshooterHatArmorItem() {
        super(24, null, 1750, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "sharpshooterhat", "sharpshootercoat", "sharpshooterboots", "sharpshootersetbonus");
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_CRIT_DAMAGE, Float.valueOf(0.15f)));
    }
}

