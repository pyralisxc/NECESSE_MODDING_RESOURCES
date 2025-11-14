/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.tween.Easings;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class SplashResidueParticle
extends ParticleOption {
    public SplashResidueParticle(Level level, float x, float y, int height, Color color) {
        super(x, y);
        if (level.isServer()) {
            return;
        }
        SplashResidueParticle.spawnParticles(level, x, y, height, color);
    }

    public SplashResidueParticle(Level level, float x, float y, Color color) {
        this(level, x, y, 64, color);
    }

    public SplashResidueParticle(Level level, float x, float y) {
        this(level, x, y, Color.lightGray);
    }

    public static void spawnParticles(Level level, float x, float y, int height, Color color) {
        SplashResidueParticle.fillCircle((float)height / 12.0f, (float)height / 60.0f * 4.0f, (index, angle, point) -> {
            float particleSizeFactor = GameRandom.globalRandom.getFloatBetween(15.0f, 35.0f);
            float particleSize = (float)height / 60.0f * particleSizeFactor;
            float actualParticleSize = GameMath.limit(particleSize / 2.0f, 10.0f, 126.0f);
            SplashResidueParticle.fillCircle(particleSize / 2.0f - actualParticleSize / 2.0f, actualParticleSize / 2.0f, (subIndex, subAngle, subPoint) -> {
                float startHeight = point.y + subPoint.y;
                SplashResidueParticle.addBaseParticle(level, x + point.x + subPoint.x, y, startHeight, actualParticleSize, color, (int)(900.0f + particleSizeFactor * 10.0f));
            });
        });
    }

    protected static int fillCircle(float radius, float pointRadius, CirclePointConsumer consumer) {
        float currentAngle = 360.0f;
        float currentRadius = 0.0f;
        int particleCount = 0;
        while (currentRadius <= radius) {
            float x = GameMath.cos(currentAngle) * currentRadius;
            float y = GameMath.sin(currentAngle) * currentRadius;
            if (currentAngle >= 360.0f) {
                currentAngle -= 360.0f;
                currentRadius += pointRadius * 0.75f;
            }
            consumer.accept(particleCount, currentAngle += (float)((double)(pointRadius * 1.5f) / ((double)currentRadius * Math.PI / 180.0)), new Point2D.Float(x, y));
            ++particleCount;
        }
        return particleCount;
    }

    protected static ParticleOption addBaseParticle(Level level, float x, float y, float startHeight, float size, Color color, int lifeTime) {
        return level.entityManager.addParticle(x, y, null).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).size((options, currentLifeTime, timeAlive, lifePercent) -> {
            int actualSize = (int)(Easings.BackOut.ease(1.0f - lifePercent) * size);
            options.size(actualSize, actualSize);
        }).height(startHeight).fadesAlpha(0.1f, 0.35f).color(color).lifeTime(lifeTime);
    }

    @FunctionalInterface
    protected static interface CirclePointConsumer {
        public void accept(int var1, float var2, Point2D.Float var3);
    }
}

