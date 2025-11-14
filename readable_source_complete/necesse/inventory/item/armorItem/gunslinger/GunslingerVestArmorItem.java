/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.gunslinger;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class GunslingerVestArmorItem
extends ChestArmorItem {
    public GunslingerVestArmorItem() {
        super(18, 1000, Item.Rarity.UNCOMMON, "gunslingervest", "gunslingerarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_ATTACK_SPEED, Float.valueOf(0.05f)));
    }
}

