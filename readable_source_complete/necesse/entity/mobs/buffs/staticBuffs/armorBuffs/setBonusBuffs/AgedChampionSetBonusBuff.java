/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class AgedChampionSetBonusBuff
extends SetBonusBuff {
    public IntUpgradeValue maxResilience = new IntUpgradeValue().setBaseValue(30).setUpgradedValue(1.0f, 30);
    public FloatUpgradeValue resilienceGain = new FloatUpgradeValue().setBaseValue(0.2f).setUpgradedValue(1.0f, 0.2f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.RESILIENCE_GAIN, this.resilienceGain.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    private void updateModifiers(ActiveBuff buff) {
        boolean fullHealth = buff.owner.getHealthPercent() == 1.0f;
        buff.setModifier(BuffModifiers.RESILIENCE_REGEN_FLAT, Float.valueOf(fullHealth ? 1.0f : 0.0f));
        buff.setModifier(BuffModifiers.MELEE_CRIT_CHANCE, Float.valueOf(fullHealth ? 0.2f : 0.0f));
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (buff.owner.buffManager.hasBuff(BuffRegistry.PERFECT_BLOCK)) {
            buff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.AGED_CHAMPION_PROWESS, buff.owner, 5000, null), false);
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "agedchampionset1"), 400);
        tooltips.add(Localization.translate("itemtooltip", "agedchampionset2"), 400);
        return tooltips;
    }
}

