/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.ravenlords;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;

public class RavenlordsChestplateArmorItem
extends ChestArmorItem {
    public RavenlordsChestplateArmorItem() {
        super(29, 1900, Item.Rarity.EPIC, "ravenlordschestplate", "ravenlordsarms", null);
        this.drawBodyPart = false;
        this.defaultLootTier = 1.0f;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.25f)), new ModifierValue<Float>(BuffModifiers.CRIT_DAMAGE, Float.valueOf(0.1f)));
    }
}

