/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.shadow;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class ShadowMantleArmorItem
extends ChestArmorItem {
    public ShadowMantleArmorItem() {
        super(18, 1300, Item.Rarity.UNCOMMON, "shadowmantle", "shadowarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f)));
    }
}

