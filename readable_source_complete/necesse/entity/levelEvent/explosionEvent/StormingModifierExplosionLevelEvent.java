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
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class StormingModifierExplosionLevelEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public StormingModifierExplosionLevelEvent() {
        this(0.0f, 0.0f, 100, new GameDamage(100.0f), false, 0.0f, null);
    }

    public StormingModifierExplosionLevelEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
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
    protected boolean canHitMob(Mob target) {
        if (target.isHostile && !this.getLevel().buffManager.getModifier(LevelModifiers.MODIFIERS_AFFECT_ENEMIES).booleanValue()) {
            return false;
        }
        return super.canHitMob(target);
    }

    @Override
    protected float getDistanceMod(float targetDistance) {
        return 1.0f;
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (GameRandom.globalRandom.getChance(0.2f)) {
            this.level.entityManager.addParticle(x + 4.0f, y - 10.0f, this.explosionTypeSwitcher.next()).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(10, 25).movesConstant(dirX * 0.8f, dirY * 0.8f).color(new Color(139, 210, 220)).height(10.0f).givesLight(75.0f, 0.5f).onProgress(0.4f, p -> {
                if (GameRandom.globalRandom.getChance(1.0f)) {
                    Point2D.Float norm = GameMath.normalize(dirX, dirY);
                    this.level.entityManager.addParticle(p.x + norm.x, p.y + norm.y, Particle.GType.IMPORTANT_COSMETIC).smokeColor().heightMoves(10.0f, 40.0f).lifeTime(lifeTime);
                }
            }).lifeTime(lifeTime);
        }
    }
}

