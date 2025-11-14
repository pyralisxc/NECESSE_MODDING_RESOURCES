/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DryadSetBonusBuff;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class DryadCrownSetBonusBuff
extends DryadSetBonusBuff {
    public IntUpgradeValue maxSummons = new IntUpgradeValue().setBaseValue(1).setUpgradedValue(1.0f, 2);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_SUMMONS, this.maxSummons.getValue(buff.getUpgradeTier()));
    }
}

