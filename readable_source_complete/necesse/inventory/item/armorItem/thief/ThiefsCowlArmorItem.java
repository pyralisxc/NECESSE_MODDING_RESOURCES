/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.thief;

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

public class ThiefsCowlArmorItem
extends SetHelmetArmorItem {
    public ThiefsCowlArmorItem() {
        super(5, DamageTypeRegistry.RANGED, 650, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.COMMON, "thiefscowl", "thiefscloak", "thiefsboots", "thiefsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
        this.canBeUsedForRaids = true;
        this.rarity = Item.Rarity.COMMON;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_ATTACK_SPEED, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.05f)));
    }
}

