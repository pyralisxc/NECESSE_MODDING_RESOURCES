/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class DemonicSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public float buffDuration = 8.0f;

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        if ((owner.dx != 0.0f || owner.dy != 0.0f) && owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), Particle.GType.COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(83, 67, 119)).height(5 + GameRandom.globalRandom.nextInt(20));
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.DEMONIC_SET_COOLDOWN);
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        ActiveBuff existingBuff = buff.owner.buffManager.getBuff(BuffRegistry.DEMONIC_RAGE);
        if (existingBuff == null) {
            int hpcost = player.getMaxHealth() / 10;
            int newhp = player.getHealth() - hpcost;
            player.setHealth(newhp);
            player.spawnDamageText(hpcost, 14, false);
            this.playBloodSound(player);
            existingBuff = new ActiveBuff(BuffRegistry.DEMONIC_RAGE, (Mob)player, this.buffDuration, null);
            buff.owner.buffManager.addBuff(existingBuff, false);
        }
    }

    protected void playBloodSound(PrimitiveSoundEmitter emitter) {
        SoundManager.playSound(GameResources.waterblob, (SoundEffect)SoundEffect.effect(emitter));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "demonicset"), 400);
        return tooltips;
    }
}

