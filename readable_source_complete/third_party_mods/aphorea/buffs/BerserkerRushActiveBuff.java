/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 */
package aphorea.buffs;

import aphorea.buffs.AdrenalineBuff;
import aphorea.items.tools.weapons.melee.battleaxe.AphBattleaxeToolItem;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.AphTimeout;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BerserkerRushActiveBuff
extends Buff {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});

    public BerserkerRushActiveBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        SoundManager.playSound((GameSound)GameResources.roar, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)buff.owner).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        buff.owner.buffManager.addBuff(new ActiveBuff(AphBuffs.STOP, buff.owner, 1.0f, null), false);
        AphTimeout.setTimeout(() -> {
            for (int i = 0; i < 40; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                buff.owner.getLevel().entityManager.addParticle(buff.owner.x - dx, buff.owner.y - dy, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(AphColors.red).heightMoves(30.0f, 10.0f).lifeTime(800);
            }
        }, 100);
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.red).height(16.0f);
        }
        if (owner.isPlayer && !(((PlayerMob)owner).getSelectedItem().item instanceof AphBattleaxeToolItem)) {
            buff.remove();
        } else {
            this.updateBuffs(buff, AdrenalineBuff.getAdrenalineLevel(buff.owner));
        }
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        Mob owner = buff.owner;
        if (owner.isPlayer && !(((PlayerMob)owner).getSelectedItem().item instanceof AphBattleaxeToolItem)) {
            buff.remove();
        } else {
            this.updateBuffs(buff, AdrenalineBuff.getAdrenalineLevel(buff.owner));
        }
    }

    public void updateBuffs(ActiveBuff buff, int level) {
        buff.setModifier(BuffModifiers.SPEED, (Object)Float.valueOf(0.2f * (float)level));
    }

    public void onRemoved(ActiveBuff buff) {
        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob)buff.owner;
            player.buffManager.addBuff(new ActiveBuff(AphBuffs.BERSERKER_RUSH_COOLDOWN, (Mob)player, 20.0f, null), false);
            SoundManager.playSound((GameSound)GameResources.explosionLight, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
            for (int i = 0; i < 40; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                player.getLevel().entityManager.addParticle((Entity)player, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(AphColors.red).heightMoves(10.0f, 30.0f).lifeTime(1000);
            }
        }
    }
}

