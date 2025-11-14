/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.nightsteel;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.IncursionBodyArmorLootTable;

public class NightsteelChestplateArmorItem
extends ChestArmorItem {
    public NightsteelChestplateArmorItem() {
        super(29, 1900, Item.Rarity.EPIC, "nightsteelchestplate", "nightsteelarms", IncursionBodyArmorLootTable.incursionBodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f)));
    }
}

