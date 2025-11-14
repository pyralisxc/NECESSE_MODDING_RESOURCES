/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BoneSpikeMobExplosionLevelEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public BoneSpikeMobExplosionLevelEvent() {
        this(0.0f, 0.0f, 100, new GameDamage(100.0f), false, 0.0f, null);
    }

    public BoneSpikeMobExplosionLevelEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.knockback = 100;
    }

    @Override
    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0f);
    }

    @Override
    protected void playExplosionEffects() {
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0f, 3.0f, true);
        SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(this.x, this.y).falloffDistance(2000));
        SoundManager.playSound(GameResources.stomp, (SoundEffect)SoundEffect.effect(this.x, this.y).falloffDistance(2000));
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
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (GameRandom.globalRandom.getChance(0.15f)) {
            this.level.entityManager.addParticle(x + 4.0f, y - 10.0f, this.explosionTypeSwitcher.next()).sprite(GameResources.particles.sprite(0, 0, 8)).sizeFades(10, 20).movesConstant(dirX * 0.2f, dirY * 0.2f).height(10.0f).color(new Color(69, 229, 193, 75)).givesLight(166.0f, 0.7f).lifeTime(2000);
        }
    }
}

