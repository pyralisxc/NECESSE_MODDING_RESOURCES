/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.AncientFossilSetBonusBuff;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class AncientFossilHelmetSetBonusBuff
extends AncientFossilSetBonusBuff
implements BuffAbility {
    public IntUpgradeValue maxResilience = new IntUpgradeValue().setBaseValue(10).setUpgradedValue(1.0f, 20);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

