/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.staticBuffs.StaminaBuff
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.GameResources
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents;

import aphorea.levelevents.ProjectileShieldLevelEvent;
import aphorea.particles.SpinelShieldParticle;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.awt.Shape;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AphSpinelShieldEvent
extends ProjectileShieldLevelEvent
implements Attacker {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
    public static float maxDelta = (float)Math.toRadians(15.0);

    public AphSpinelShieldEvent() {
    }

    public AphSpinelShieldEvent(Mob owner, float angle) {
        super(owner, angle, new GameRandom());
    }

    public void init() {
        super.init();
        if (this.isClient()) {
            SoundManager.playSound((GameSound)GameResources.cling, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.owner));
            for (int i = 0; i < 20; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                this.owner.getLevel().entityManager.addParticle((Entity)this.owner, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_light, AphColors.spinel})).heightMoves(10.0f, 20.0f, 2.0f, 0.5f, 0.0f, 0.0f).lifeTime(500);
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!this.owner.buffManager.hasBuff("spinelshieldactive")) {
            this.over();
        }
        this.getLevel().entityManager.addParticle((Particle)new SpinelShieldParticle(this.getLevel(), this.owner, this.angle), Particle.GType.CRITICAL);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!this.owner.buffManager.hasBuff("spinelshieldactive")) {
            this.over();
        }
    }

    public Shape getHitBox() {
        return this.getShieldHitBox(20.0f, 80.0f, 40.0f, -40.0f);
    }

    public boolean canHit(Mob mob) {
        return mob == null || super.canHit(mob) && mob.isHostile;
    }

    public void clientHit(Mob mob) {
        float modifier = mob.getKnockbackModifier();
        if (modifier != 0.0f) {
            StaminaBuff.useStaminaAndGetValid((Mob)this.owner, (float)0.01f);
            SoundManager.playSound((GameSound)GameResources.cling, (SoundEffect)SoundEffect.effect((float)mob.x, (float)mob.y).volume(0.25f));
            for (int i = 0; i < 5; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                mob.getLevel().entityManager.addParticle(mob.x, mob.y, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_light, AphColors.spinel})).heightMoves(10.0f, 20.0f, 2.0f, 0.5f, 0.0f, 0.0f).lifeTime(500);
            }
        }
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted) {
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, target, 0.2f, (Attacker)this), true);
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0f) {
                StaminaBuff.useStaminaAndGetValid((Mob)this.owner, (float)0.01f);
                float knockback = 50.0f / modifier;
                target.isServerHit(new GameDamage(0.0f), target.x - this.owner.x, target.y - this.owner.y, knockback, (Attacker)this.owner);
            }
        }
    }

    @Override
    protected void onProjectileHit(Projectile projectile) {
        StaminaBuff.useStaminaAndGetValid((Mob)this.owner, (float)0.05f);
        if (projectile.isClient()) {
            SoundManager.playSound((GameSound)GameResources.cling, (SoundEffect)SoundEffect.effect((float)projectile.x, (float)projectile.y).volume(0.5f));
            for (int i = 0; i < 10; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                projectile.getLevel().entityManager.addParticle(projectile.x, projectile.y, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_light, AphColors.spinel})).heightMoves(10.0f, 20.0f, 2.0f, 0.5f, 0.0f, 0.0f).lifeTime(500);
            }
        }
        projectile.remove();
    }

    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public float getMaxDelta() {
        return maxDelta;
    }
}

