/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SpideriteSetBonusBuff;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class SpideriteCrownSetBonusBuff
extends SpideriteSetBonusBuff {
    public IntUpgradeValue maxSummons = new IntUpgradeValue().setBaseValue(2).setUpgradedValue(1.0f, 2);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.setModifier(BuffModifiers.MAX_SUMMONS, this.maxSummons.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).excludeLimits(BuffModifiers.SLOW).buildToStatList(list);
    }
}

