/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierUpgradeValue;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.inventory.item.ItemStatTip;

public class SimpleUpgradeSetBonusBuff
extends SetBonusBuff {
    protected ModifierUpgradeValue<?>[] modifiers;

    public SimpleUpgradeSetBonusBuff(ModifierUpgradeValue<?> ... modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        for (ModifierUpgradeValue<?> modifier : this.modifiers) {
            buff.setModifier(modifier.modifier, modifier.value.getValue(buff.getUpgradeTier()));
            if (modifier.limits.hasMin()) {
                buff.setMinModifier(modifier.modifier, modifier.limits.min().getValue(buff.getUpgradeTier()));
            }
            if (!modifier.limits.hasMax()) continue;
            buff.setMaxModifier(modifier.modifier, modifier.limits.max().getValue(buff.getUpgradeTier()));
        }
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        for (ModifierUpgradeValue<?> modifier : this.modifiers) {
            ModifierTooltip maxTooltip;
            ModifierTooltip minTooltip;
            ModifierTooltip tooltip = currentValues.getModifierTooltip(modifier.modifier, lastValues);
            if (tooltip != null) {
                list.add(tooltip.tip);
            }
            if ((minTooltip = currentValues.getMinLimitTooltip(modifier.modifier, lastValues, false)) != null) {
                list.add(minTooltip.tip);
            }
            if ((maxTooltip = currentValues.getMaxLimitTooltip(modifier.modifier, lastValues, false)) == null) continue;
            list.add(maxTooltip.tip);
        }
    }
}

