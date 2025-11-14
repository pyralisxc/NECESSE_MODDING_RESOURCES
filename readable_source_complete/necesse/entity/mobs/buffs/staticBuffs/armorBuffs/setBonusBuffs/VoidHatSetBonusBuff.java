/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class VoidHatSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public IntUpgradeValue maxMana = new IntUpgradeValue(0, 0.1f).setBaseValue(100).setUpgradedValue(1.0f, 250);

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        if (owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(65, 30, 109)).height(16.0f);
        }
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxMana.getValue(buff.getUpgradeTier()));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "voidset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        Mob owner = buff.owner;
        owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, owner, 3.0f, null), false);
        owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.VOID_SET_COOLDOWN, owner, 30.0f, null), false);
        if (owner.isClient()) {
            for (int i = 0; i < 12; ++i) {
                player.getLevel().entityManager.addParticle(player.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), player.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(player.dx, player.dy).color(new Color(65, 30, 109)).height(16.0f);
            }
            SoundManager.playSound(GameResources.teleportfail, (SoundEffect)SoundEffect.effect(owner).pitch(1.3f));
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.VOID_SET_COOLDOWN.getID());
    }
}

