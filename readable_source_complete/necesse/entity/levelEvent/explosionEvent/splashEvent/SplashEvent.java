/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SplashResidueParticle;
import necesse.gfx.GameResources;

public abstract class SplashEvent
extends ExplosionEvent {
    protected boolean isLiquid = false;

    public SplashEvent() {
        this(0.0f, 0.0f, 96, new GameDamage(0.0f), false, 0.0f, null);
    }

    public SplashEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.knockback = 0;
        this.isLiquid = true;
        this.destroysGrass = false;
    }

    @Override
    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 0.4f;
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        this.level.entityManager.addParticle(x, y, Particle.GType.CRITICAL).movesConstant(dirX * 0.1f, dirY * 0.1f).color(this.getInnerSplashColor()).height(10.0f).sizeFades(12, 18).givesLight(250.0f, 0.3f).onProgress(0.1f, p -> {
            Point2D.Float norm = GameMath.normalize(dirX, dirY);
            this.level.entityManager.addParticle(p.x + norm.x * 20.0f, p.y + norm.y * 20.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(dirX, dirY).color(this.getOuterSplashColor()).sizeFades(10, 14).heightMoves(10.0f, 30.0f).lifeTime(lifeTime);
        }).lifeTime((int)((float)lifeTime * 1.4f));
        if (this.isLiquid && GameRandom.globalRandom.getEveryXthChance(20)) {
            SplashResidueParticle puddle = new SplashResidueParticle(this.level, x, y - 5.0f, this.getInnerSplashColor());
            this.level.entityManager.addParticle(puddle, Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.splash, (SoundEffect)SoundEffect.effect(this.x, this.y).falloffDistance(1200).volume(0.1f).pitch(GameRandom.globalRandom.getFloatBetween(1.15f, 1.25f)));
        SoundManager.playSound(GameResources.shatter1, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(1.15f, 1.25f)));
    }

    @Override
    protected float getParticleRangeModifier() {
        return 0.15f;
    }

    @Override
    protected boolean canHitMob(Mob target) {
        return super.canHitMob(target) && !target.isHuman;
    }

    protected abstract Color getInnerSplashColor();

    protected Color getOuterSplashColor() {
        return this.getInnerSplashColor().brighter().brighter();
    }
}

