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

public class SmokePuffParticle
extends ParticleOption {
    public SmokePuffParticle(Level level, float x, float y, int height, Color color) {
        super(x, y);
        if (level.isServer()) {
            return;
        }
        SmokePuffParticle.spawnParticles(level, x, y, height, color);
    }

    public SmokePuffParticle(Level level, float x, float y, Color color) {
        this(level, x, y, 64, color);
    }

    public SmokePuffParticle(Level level, float x, float y) {
        this(level, x, y, Color.lightGray);
    }

    public static void spawnParticles(Level level, float x, float y, int height, Color color) {
        SmokePuffParticle.fillCircle((float)height / 6.0f, (float)height / 60.0f * 5.0f, (index, angle, point) -> {
            float particleSize = GameMath.min((float)height / 2.0f, (float)height / 4.0f + (float)height / 6.0f * ((float)index / 3.0f));
            float actualParticleSize = GameMath.limit(particleSize / 2.0f, 20.0f, 26.0f);
            SmokePuffParticle.fillCircle(particleSize / 2.0f - actualParticleSize / 2.0f, actualParticleSize / 2.0f, (subIndex, subAngle, subPoint) -> {
                float startHeight = point.y + subPoint.y;
                ParticleOption particleOption = SmokePuffParticle.addBaseParticle(level, x + point.x + subPoint.x, y, startHeight, actualParticleSize, color, 1100 + GameRandom.globalRandom.getIntBetween(-100, 100));
                if (index <= 3) {
                    SmokePuffParticle.setupForCenterTrail(particleOption, x + subPoint.x, y, startHeight, startHeight + (float)height / 3.0f * 2.0f * ((float)index / 3.0f));
                } else {
                    SmokePuffParticle.setupForTopCloud(particleOption, startHeight, height, angle, (float)height / 12.0f).lifeTime(particleOption.lifeTime + 200);
                }
            });
        });
        SmokePuffParticle.fillCircle((float)height / 12.0f, (float)height / 60.0f * 4.0f, (index, angle, point) -> {
            float particleSizeFactor = GameRandom.globalRandom.getFloatBetween(10.0f, 15.0f);
            float particleSize = (float)height / 60.0f * particleSizeFactor;
            float actualParticleSize = GameMath.limit(particleSize / 2.0f, 10.0f, 26.0f);
            float distance = GameRandom.globalRandom.getFloatBetween((float)height / 4.0f, (float)height / 3.0f);
            SmokePuffParticle.fillCircle(particleSize / 2.0f - actualParticleSize / 2.0f, actualParticleSize / 2.0f, (subIndex, subAngle, subPoint) -> {
                float startHeight = point.y + subPoint.y;
                ParticleOption particleOption = SmokePuffParticle.addBaseParticle(level, x + point.x + subPoint.x, y, startHeight, actualParticleSize, color, (int)(900.0f + particleSizeFactor * 10.0f));
                SmokePuffParticle.setupBottomSpread(particleOption, startHeight, 45 * index, distance);
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

    protected static ParticleOption setupForCenterTrail(ParticleOption option, float centerX, float centerY, float startHeight, float toHeight) {
        option.changePos(centerX, centerY);
        return option.height((float delta, int lifeTime, int timeAlive, float lifePercent) -> GameMath.lerp(Easings.CircOut.ease(lifePercent), startHeight, toHeight)).rotates(10.0f, 30.0f);
    }

    protected static ParticleOption setupForTopCloud(ParticleOption option, float startHeight, float toHeight, float angle, float radius) {
        float heightDiff = Math.abs(GameMath.sin(angle)) * radius + toHeight - radius;
        float startX = option.getPos().x;
        float endX = startX + GameMath.cos(angle) * radius;
        return option.moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            pos.x = GameMath.lerp(lifePercent, startX, endX);
        }).height((float delta, int lifeTime, int timeAlive, float lifePercent) -> startHeight + heightDiff * Easings.CircOut.ease(lifePercent)).rotates(-GameMath.cos(angle) * 100.0f, -GameMath.cos(angle) * 100.0f);
    }

    protected static ParticleOption setupBottomSpread(ParticleOption option, float startHeight, float angle, float radius) {
        float endHeight = startHeight + Math.abs(GameMath.sin(angle)) * radius;
        float startX = option.getPos().x;
        float endX = startX + GameMath.cos(angle) * radius;
        return option.moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            pos.x = GameMath.lerp(Easings.CubicOut.ease(lifePercent), startX, endX);
        }).height((float delta, int lifeTime, int timeAlive, float lifePercent) -> startHeight + endHeight * lifePercent).rotates(-GameMath.cos(angle) * 100.0f, -GameMath.cos(angle) * 100.0f);
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

