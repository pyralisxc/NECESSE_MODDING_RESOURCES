/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.widow;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class WidowChestplateArmorItem
extends ChestArmorItem {
    public WidowChestplateArmorItem() {
        super(25, 1600, Item.Rarity.UNCOMMON, "widowchest", "widowarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MAGIC_DAMAGE, Float.valueOf(0.1f)));
    }
}

