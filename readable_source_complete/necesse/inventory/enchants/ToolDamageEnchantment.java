/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.enchants;

import necesse.engine.modifiers.ModifierValue;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;

public class ToolDamageEnchantment
extends ToolItemEnchantment {
    public static final ToolDamageEnchantment noEnchant = new ToolDamageEnchantment(0, new ModifierValue[0]);

    public ToolDamageEnchantment(int enchantCostModPercent, ModifierValue<?> ... values) {
        super(ToolItemModifiers.LIST, enchantCostModPercent, values);
    }

    static {
        ToolDamageEnchantment.noEnchant.idData.setData(0, "noenchant");
    }
}

