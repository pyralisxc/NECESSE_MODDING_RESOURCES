/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class CursedCroneSpiritBeamsExplosionLevelEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    public boolean hasGeneratedASoul;
    public boolean generateSoulsOnHit;

    public CursedCroneSpiritBeamsExplosionLevelEvent() {
        this(0.0f, 0.0f, 100, new GameDamage(100.0f), false, 0.0f, null, false);
    }

    public CursedCroneSpiritBeamsExplosionLevelEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner, boolean generateSoulsOnHit) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.knockback = 100;
        this.hasGeneratedASoul = false;
        this.generateSoulsOnHit = generateSoulsOnHit;
    }

    @Override
    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0f);
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.magicExplosion, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.4f).pitch(1.0f));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 2.0f, 2.0f, true);
    }

    @Override
    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange);
    }

    @Override
    protected float getDistanceMod(float targetDistance) {
        return 1.0f;
    }

    @Override
    protected boolean canHitMob(Mob target) {
        return super.canHitMob(target) && target != this.ownerMob;
    }

    @Override
    protected void onMobWasHit(Mob mob, float distance) {
        super.onMobWasHit(mob, distance);
        if (!this.hasGeneratedASoul && this.generateSoulsOnHit && this.ownerMob != null) {
            this.hasGeneratedASoul = true;
            this.ownerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.SOULSTORM_SOULS, this.ownerMob, 6.0f, null), true);
        }
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (GameRandom.globalRandom.getChance(0.1f)) {
            this.level.entityManager.addParticle(x + 4.0f, y - 10.0f, this.explosionTypeSwitcher.next()).sprite(GameResources.particles.sprite(0, 0, 8)).sizeFades(8, 14).movesConstant(dirX * 0.2f, dirY * 0.2f).height(10.0f).color(new Color(69, 229, 193, 185)).givesLight(166.0f, 0.7f).lifeTime(2000);
        }
    }
}

