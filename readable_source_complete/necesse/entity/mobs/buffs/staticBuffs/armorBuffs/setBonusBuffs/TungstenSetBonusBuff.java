/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class TungstenSetBonusBuff
extends SetBonusBuff {
    public IntUpgradeValue maxResilience = new IntUpgradeValue().setBaseValue(30).setUpgradedValue(1.0f, 30);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(buff.getUpgradeTier()));
        buff.setMaxModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.0f));
        buff.setModifier(BuffModifiers.KNOCKBACK_OUT, Float.valueOf(0.5f));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "tungstenset1"));
        tooltips.add(Localization.translate("itemtooltip", "tungstenset2"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).excludeModifiers(BuffModifiers.KNOCKBACK_OUT).excludeLimits(BuffModifiers.KNOCKBACK_INCOMING_MOD).buildToStatList(list);
    }
}

