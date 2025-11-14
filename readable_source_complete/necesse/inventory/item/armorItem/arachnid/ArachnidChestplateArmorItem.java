/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.arachnid;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class ArachnidChestplateArmorItem
extends ChestArmorItem {
    public ArachnidChestplateArmorItem() {
        super(7, 550, Item.Rarity.UNCOMMON, "arachnidchestplate", "arachnidarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SUMMON_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.5f)));
    }
}

