/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.deepfrost;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class DeepfrostChestplateArmorItem
extends ChestArmorItem {
    public DeepfrostChestplateArmorItem() {
        super(23, 1450, Item.Rarity.UNCOMMON, "deepfrostchestplate", "deepfrostarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.PROJECTILE_VELOCITY, Float.valueOf(0.1f)));
    }
}

