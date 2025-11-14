/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.geom.Point2D;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class FlamelingsModifierSmokePuffLevelEvent
extends ExplosionEvent
implements Attacker {
    public FlamelingsModifierSmokePuffLevelEvent() {
        this(0.0f, 0.0f, 100, new GameDamage(100.0f), false, 0.0f, null);
    }

    public FlamelingsModifierSmokePuffLevelEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
    }

    @Override
    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0f);
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.swoosh, (SoundEffect)SoundEffect.effect(this.x, this.y));
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
        if (GameRandom.globalRandom.getChance(0.5f)) {
            Point2D.Float norm = GameMath.normalize(dirX, dirY);
            this.level.entityManager.addParticle(x + norm.x * range, y + norm.y * range, Particle.GType.IMPORTANT_COSMETIC).movesConstant(dirX, dirY).smokeColor().heightMoves(10.0f, 40.0f).lifeTime(lifeTime);
        }
    }
}

