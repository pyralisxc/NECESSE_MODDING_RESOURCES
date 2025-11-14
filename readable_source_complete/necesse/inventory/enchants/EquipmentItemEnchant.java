/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.enchants;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.enchants.ItemEnchantment;

public class EquipmentItemEnchant
extends ItemEnchantment {
    public static final EquipmentItemEnchant noEnchant = new EquipmentItemEnchant(0, new ModifierValue[0]);

    public EquipmentItemEnchant(int enchantCostModPercent, ModifierValue ... values) {
        super(BuffModifiers.LIST, enchantCostModPercent);
        for (ModifierValue value : values) {
            value.apply(this);
        }
    }

    static {
        EquipmentItemEnchant.noEnchant.idData.setData(0, "noenchant");
    }
}

