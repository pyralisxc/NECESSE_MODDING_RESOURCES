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
import necesse.entity.mobs.hostile.BloatedSpiderMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BloatedSpiderExplosionEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public BloatedSpiderExplosionEvent() {
        this(0.0f, 0.0f, 150, BloatedSpiderMob.explosionDamage, false, 0.0f, null);
    }

    public BloatedSpiderExplosionEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
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
        float dy;
        float dx;
        if (GameRandom.globalRandom.getChance(0.7f) && range < 25.0f) {
            dx = dirX * (float)GameRandom.globalRandom.getIntBetween(20, 70);
            dy = dirY * (float)GameRandom.globalRandom.getIntBetween(10, 60) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 80).movesFriction(dx * 0.05f, dy * 0.05f, 0.8f).color(new Color(89, 89, 89)).heightMoves(0.0f, 70.0f).lifeTime(lifeTime * 4);
        }
        if (range <= (float)Math.max(this.range - 125, 25)) {
            dx = dirX * (float)GameRandom.globalRandom.getIntBetween(140, 150);
            dy = dirY * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 50).movesFriction(dx * 0.05f, dy * 0.05f, 0.8f).color(new Color(255, 255, 255)).heightMoves(0.0f, 10.0f).lifeTime(lifeTime * 3);
        }
        if (GameRandom.globalRandom.getChance(0.5f)) {
            this.level.entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(25, 40).movesConstant(dirX * 0.8f, dirY * 0.8f).flameColor(75.0f).height(10.0f).givesLight(75.0f, 0.5f).onProgress(0.4f, p -> {
                Point2D.Float norm = GameMath.normalize(dirX, dirY);
                this.level.entityManager.addParticle(p.x + norm.x * 20.0f, p.y + norm.y * 20.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(dirX, dirY).smokeColor().heightMoves(10.0f, 40.0f).lifeTime(lifeTime);
            }).lifeTime(lifeTime);
        }
    }
}

