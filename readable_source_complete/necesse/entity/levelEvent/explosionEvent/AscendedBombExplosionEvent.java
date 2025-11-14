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
import necesse.entity.mobs.hostile.BloatedSpiderMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class AscendedBombExplosionEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public AscendedBombExplosionEvent() {
        this(0.0f, 0.0f, 150, BloatedSpiderMob.explosionDamage, false, 0.0f, null);
    }

    public AscendedBombExplosionEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
    }

    @Override
    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(1000.0f);
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(2.0f).pitch(1.5f));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0f, 3.0f, true);
    }

    @Override
    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 1.5f;
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (range <= (float)Math.max(this.range - 125, 25)) {
            float dx = dirX * (float)GameRandom.globalRandom.getIntBetween(140, 150);
            float dy = dirY * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sizeFades(10, 20).color(new Color(255, 0, 231)).movesFriction(dx * 0.05f, dy * 0.05f, 0.8f).heightMoves(0.0f, 10.0f).ignoreLight(true).lifeTime(lifeTime * 3);
        }
        if (GameRandom.globalRandom.getChance(0.5f)) {
            this.level.entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(25, 40).movesConstant(dirX * 0.8f, dirY * 0.8f).height(10.0f).ignoreLight(true).lifeTime(lifeTime);
        }
    }
}

