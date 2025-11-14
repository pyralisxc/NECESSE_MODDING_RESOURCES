/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderSpitEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class IvyHelmetSetBonusBuff
extends SetBonusBuff {
    public FloatUpgradeValue spitDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(35.0f).setUpgradedValue(1.0f, 75.0f);
    public IntUpgradeValue maxResilience = new IntUpgradeValue().setBaseValue(20).setUpgradedValue(1.0f, 30);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent wasHitEvent) {
        super.onWasHit(buff, wasHitEvent);
        if (!wasHitEvent.wasPrevented && buff.owner.isServer()) {
            CaveSpiderSpitEvent event = new CaveSpiderSpitEvent(buff.owner, buff.owner.getX(), buff.owner.getY(), GameRandom.globalRandom, GiantCaveSpiderMob.Variant.SWAMP, new GameDamage(DamageTypeRegistry.MELEE, this.spitDamage.getValue(buff.getUpgradeTier()).floatValue()), 6);
            buff.owner.getLevel().entityManager.events.add(event);
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ivyhelmetset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

