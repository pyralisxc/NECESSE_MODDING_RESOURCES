/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.thief;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class ThiefsCloakArmorItem
extends ChestArmorItem {
    public ThiefsCloakArmorItem() {
        super(7, 650, "thiefscloak", "thiefsarms", BodyArmorLootTable.bodyArmor);
        this.rarity = Item.Rarity.COMMON;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_DAMAGE, Float.valueOf(0.05f)));
    }
}

