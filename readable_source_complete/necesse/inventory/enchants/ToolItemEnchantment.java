/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.enchants;

import necesse.engine.modifiers.ModifierList;
import necesse.engine.modifiers.ModifierValue;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;

public class ToolItemEnchantment
extends ItemEnchantment {
    public static final ToolItemEnchantment noEnchant = new ToolItemEnchantment(0, new ModifierValue[0]);

    public ToolItemEnchantment(ModifierList list, int enchantCostModPercent, ModifierValue<?> ... values) {
        super(list, enchantCostModPercent);
        if (list != ToolItemModifiers.LIST) {
            throw new IllegalArgumentException("Modifier list must be child of ToolItemModifiers list");
        }
        for (ModifierValue<?> value : values) {
            value.apply(this);
        }
    }

    public ToolItemEnchantment(int enchantCostModPercent, ModifierValue<?> ... values) {
        this(ToolItemModifiers.LIST, enchantCostModPercent, values);
    }

    static {
        ToolItemEnchantment.noEnchant.idData.setData(0, "noenchant");
    }
}

