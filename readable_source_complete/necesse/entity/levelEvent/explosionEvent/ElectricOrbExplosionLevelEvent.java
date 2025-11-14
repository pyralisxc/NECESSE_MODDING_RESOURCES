/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
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

public class ElectricOrbExplosionLevelEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public ElectricOrbExplosionLevelEvent() {
        this(0.0f, 0.0f, 100, new GameDamage(100.0f), false, 0.0f, null);
        this.knockback = 0;
    }

    public ElectricOrbExplosionLevelEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.knockback = 0;
        this.hitsOwner = false;
    }

    @Override
    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0f);
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.fizz, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(1.5f).pitch(1.5f));
        SoundManager.playSound(GameResources.electricExplosion, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(1.5f).pitch(1.0f));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0f, 3.0f, true);
    }

    @Override
    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 1.5f;
    }

    @Override
    protected float getDistanceMod(float targetDistance) {
        return 1.0f;
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        GameRandom rnd = GameRandom.globalRandom;
        if (rnd.getChance(0.2f)) {
            this.level.entityManager.addParticle(x + 4.0f, y - 10.0f, this.explosionTypeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(rnd.nextInt(4), 0, 22)).sizeFades(10, 25).movesConstant(dirX * 0.8f, dirY * 0.8f).color(new Color(95, 205, 228)).givesLight(190.0f, 0.9f).height(10.0f).onProgress(0.4f, p -> {
                if (rnd.getChance(1.0f)) {
                    Point2D.Float norm = GameMath.normalize(dirX, dirY);
                    this.level.entityManager.addParticle(p.x + norm.x, p.y + norm.y, Particle.GType.IMPORTANT_COSMETIC).smokeColor().heightMoves(10.0f, 40.0f).lifeTime(lifeTime);
                }
            }).lifeTime(lifeTime);
        }
    }
}

