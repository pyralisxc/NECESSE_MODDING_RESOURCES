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
import necesse.entity.mobs.hostile.bosses.SunlightChampionMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class SupernovaExplosionEvent
extends ExplosionEvent
implements Attacker {
    private int particleBuffer;
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public SupernovaExplosionEvent() {
        this(0.0f, 0.0f, 650, SunlightChampionMob.supernovaDamage, 0.0f, null);
    }

    public SupernovaExplosionEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner) {
        super(x, y, range, damage, false, toolTier, owner);
        this.targetRangeMod = 0.0f;
    }

    @Override
    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0f);
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(2.5f).pitch(1.5f));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0f, 3.0f, true);
    }

    @Override
    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 1.5f;
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (this.particleBuffer < 10) {
            ++this.particleBuffer;
        } else {
            this.particleBuffer = 0;
            if (range <= (float)Math.max(this.range - 125, 25)) {
                float dx = dirX * (float)GameRandom.globalRandom.getIntBetween(140, 150);
                float dy = dirY * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8f;
                this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 100).givesLight(53.0f, 1.0f).movesFriction(dx * 0.05f, dy * 0.05f, 0.8f).color((options, lifeTime1, timeAlive, lifePercent) -> {
                    float clampedLifePercent = Math.max(0.0f, Math.min(1.0f, lifePercent));
                    options.color(new Color((int)(255.0f - 55.0f * clampedLifePercent), (int)(225.0f - 200.0f * clampedLifePercent), (int)(155.0f - 125.0f * clampedLifePercent)));
                }).heightMoves(0.0f, 10.0f).lifeTime(lifeTime * 3);
            }
        }
    }
}

