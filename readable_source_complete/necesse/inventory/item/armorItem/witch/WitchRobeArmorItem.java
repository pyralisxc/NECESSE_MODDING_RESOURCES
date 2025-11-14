/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.witch;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class WitchRobeArmorItem
extends ChestArmorItem {
    public WitchRobeArmorItem() {
        super(14, 850, Item.Rarity.UNCOMMON, "witchrobe", "witchrobearms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MAGIC_DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.SUMMON_DAMAGE, Float.valueOf(0.05f)));
    }
}

