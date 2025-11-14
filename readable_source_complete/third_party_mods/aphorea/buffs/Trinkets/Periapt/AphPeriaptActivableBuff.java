/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.network.Packet
 *  necesse.engine.registries.BuffRegistry
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
 *  necesse.entity.mobs.buffs.ActiveBuffAbility
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.inventory.item.ItemStatTip
 */
package aphorea.buffs.Trinkets.Periapt;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphTimeout;
import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
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
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.item.ItemStatTip;

public abstract class AphPeriaptActivableBuff
extends TrinketBuff
implements ActiveBuffAbility {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
    public String activeBuff;

    public AphPeriaptActivableBuff(String activeBuff) {
        this.activeBuff = activeBuff;
    }

    public abstract Color getColor();

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues((ModifierContainer)lastValues).buildToStatList(list);
    }

    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !player.buffManager.hasBuff(AphBuffs.PERIAPT_ACTIVE) && !player.buffManager.hasBuff(AphBuffs.PERIAPT_COOLDOWN);
    }

    public void onActiveAbilityStarted(PlayerMob player, ActiveBuff buff, Packet content) {
        player.buffManager.addBuff(new ActiveBuff(AphBuffs.PERIAPT_ACTIVE, (Mob)player, 11.0f, null), false);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)this.activeBuff), (Mob)player, 11.0f, null), false);
        SoundManager.playSound((GameSound)GameResources.magicroar, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        player.buffManager.addBuff(new ActiveBuff(AphBuffs.STOP, (Mob)player, 1.0f, null), false);
        AphTimeout.setTimeout(() -> {
            for (int i = 0; i < 40; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                player.getLevel().entityManager.addParticle(player.x - dx, player.y - dy, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(this.getColor()).heightMoves(30.0f, 10.0f).lifeTime(800);
            }
        }, 100);
    }

    public void onActiveAbilityUpdate(PlayerMob player, ActiveBuff buff, Packet content) {
    }

    public boolean tickActiveAbility(PlayerMob player, ActiveBuff buff, boolean isRunningClient) {
        int angle = 180 + (int)(GameRandom.globalRandom.nextFloat() * 30.0f) - 15;
        float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
        float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
        player.getLevel().entityManager.addParticle((Entity)player, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(this.getColor()).heightMoves(10.0f, 30.0f).lifeTime(500);
        return player.buffManager.hasBuff(this.activeBuff);
    }

    public void onActiveAbilityStopped(PlayerMob player, ActiveBuff buff) {
        if (player.getLevel() != null) {
            SoundManager.playSound((GameSound)GameResources.explosionLight, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
            for (int i = 0; i < 40; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                player.getLevel().entityManager.addParticle((Entity)player, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(this.getColor()).heightMoves(10.0f, 30.0f).lifeTime(1000);
            }
            if (player.buffManager.hasBuff(this.activeBuff)) {
                player.buffManager.removeBuff(this.activeBuff, false);
            }
        }
    }
}

