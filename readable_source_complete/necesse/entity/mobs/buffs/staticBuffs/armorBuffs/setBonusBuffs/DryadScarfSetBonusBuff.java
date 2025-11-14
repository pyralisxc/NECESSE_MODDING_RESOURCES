/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DryadSetBonusBuff;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class DryadScarfSetBonusBuff
extends DryadSetBonusBuff {
    public IntUpgradeValue armorPen = new IntUpgradeValue().setBaseValue(10).setUpgradedValue(1.0f, 20);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ARMOR_PEN_FLAT, this.armorPen.getValue(buff.getUpgradeTier()));
    }
}

