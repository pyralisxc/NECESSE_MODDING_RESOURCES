/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;

public class SoulseedExplosionEvent
extends ExplosionEvent
implements Attacker {
    protected Color particleColor;
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public SoulseedExplosionEvent() {
        this(0.0f, 0.0f, 60, new GameDamage(100.0f), false, 0.0f, null);
    }

    public SoulseedExplosionEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.particleColor = this.setParticleColor();
    }

    @Override
    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0f);
    }

    @Override
    protected float getDistanceMod(float targetDistance) {
        return 1.0f;
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.5f).pitch(1.8f));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 2.0f, 2.0f, true);
    }

    @Override
    protected boolean canHitMob(Mob target) {
        return super.canHitMob(target) && target != this.ownerMob;
    }

    @Override
    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 0.7f;
    }

    protected Color setParticleColor() {
        return ThemeColorRegistry.SOULSEED.getRandomColor();
    }

    @Override
    public void clientTick() {
        if (this.tickCounter == 0) {
            this.playExplosionEffects();
        }
        int adjustedRange = this.range - this.range / 10;
        float lastRange = Math.max(0.0f, (float)adjustedRange * ((float)(this.tickCounter - 2) / (float)this.maxTicks));
        float range = (float)adjustedRange * ((float)this.tickCounter / (float)this.maxTicks);
        int particles = (int)this.getParticleCount(range, lastRange);
        SoulseedExplosionEvent.spawnExplosionParticles(this.level, this.x, this.y, particles, lastRange, range * this.getParticleRangeModifier(), (level, x, y, dirX, dirY, lifeTime, currentRange) -> this.spawnExplosionParticle(x, y, dirX, dirY, lifeTime, currentRange));
        ++this.tickCounter;
        if (this.tickCounter > this.maxTicks) {
            this.over();
        }
    }

    protected void spawnCenterParticles(float x, float y, float dirX, float dirY, int lifeTime, float range) {
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        int moveMin = (int)(range * 0.05f);
        int moveMax = (int)(range * 0.4f);
        if (GameRandom.globalRandom.getChance(0.2f)) {
            float dx = dirX * (float)GameRandom.globalRandom.getIntBetween(moveMin, moveMax);
            float dy = dirY * (float)GameRandom.globalRandom.getIntBetween(moveMin, moveMax) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(14, 40).movesFriction(dx * 0.04f, dy * 0.02f, 0.4f).color(ThemeColorRegistry.SMOKE.getRandomColor()).heightMoves(0.0f, 8.0f).lifeTime(lifeTime);
        }
        if (GameRandom.globalRandom.getChance(1.0f)) {
            this.level.entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(4, GameMath.max(18, moveMax)).movesConstant(dirX * 0.8f, dirY * 0.8f).color(ThemeColorRegistry.SOULSEED.getRandomColor()).height(8.0f).givesLight(75.0f, 0.5f).lifeTime(lifeTime);
        }
    }
}

