/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DryadSetBonusBuff;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class DryadHatSetBonusBuff
extends DryadSetBonusBuff {
    public IntUpgradeValue maxManaFlat = new IntUpgradeValue().setBaseValue(200).setUpgradedValue(1.0f, 250);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxManaFlat.getValue(buff.getUpgradeTier()));
    }
}

