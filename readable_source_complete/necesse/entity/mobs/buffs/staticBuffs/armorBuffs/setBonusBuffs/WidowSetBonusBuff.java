/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class WidowSetBonusBuff
extends SetBonusBuff {
    public IntUpgradeValue maxMana = new IntUpgradeValue(0, 0.1f).setBaseValue(250).setUpgradedValue(1.0f, 250);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMaxModifier(BuffModifiers.SLOW, Float.valueOf(0.0f));
        buff.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxMana.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        super.onHasAttacked(buff, event);
        if (!event.wasPrevented) {
            event.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.WIDOW_POISON, event.target, 5.0f, event.attacker), event.target.isServer());
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "widowset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).excludeLimits(BuffModifiers.SLOW).buildToStatList(list);
    }
}

