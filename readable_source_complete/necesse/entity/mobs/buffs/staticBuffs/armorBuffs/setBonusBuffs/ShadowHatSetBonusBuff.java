/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class ShadowHatSetBonusBuff
extends SetBonusBuff {
    public FloatUpgradeValue speed = new FloatUpgradeValue().setBaseValue(0.05f).setUpgradedValue(1.0f, 0.1f);
    public FloatUpgradeValue projectileVelocity = new FloatUpgradeValue().setBaseValue(0.15f).setUpgradedValue(1.0f, 0.3f);
    public IntUpgradeValue maxMana = new IntUpgradeValue(0, 0.1f).setBaseValue(200).setUpgradedValue(1.0f, 250);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SPEED, this.speed.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.PROJECTILE_VELOCITY, this.projectileVelocity.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxMana.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        super.onHasAttacked(buff, event);
        if (!event.wasPrevented && event.damageType == DamageTypeRegistry.MAGIC) {
            event.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.HAUNTED, event.target, 5.0f, event.attacker), event.target.isServer());
        }
    }

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        if (owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(33, 35, 42)).height(16.0f);
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "shadowhatset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

