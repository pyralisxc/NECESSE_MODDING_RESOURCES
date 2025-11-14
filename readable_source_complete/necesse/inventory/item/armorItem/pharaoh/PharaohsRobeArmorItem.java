/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.pharaoh;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class PharaohsRobeArmorItem
extends ChestArmorItem {
    public PharaohsRobeArmorItem() {
        super(14, 1000, Item.Rarity.UNCOMMON, "pharaohsrobe", "pharaohsrobearms", BodyArmorLootTable.bodyArmor);
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.UNDER_FACIAL_FEATURE;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.05f)));
    }
}

