/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle.fireworks;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksPath;
import necesse.entity.particle.fireworks.FireworksRocketParticle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class FireworksExplosion {
    public static FireworksRocketParticle.ParticleGetter<FireworksPath> popPath = (particle, progress, random) -> {
        float x = random.floatGaussian() * 10.0f;
        float y = random.floatGaussian() * 10.0f;
        float heightPart = 0.25f;
        return new FireworksPath(x, y * (1.0f - heightPart), y * heightPart);
    };
    public static FireworksExplosion simplePopExplosion = new FireworksExplosion(popPath);
    public int particles = 300;
    public int lifetime = 2000;
    public int minSize = 10;
    public int maxSize = 18;
    public float particleLightHue = ParticleOption.defaultFlameHue;
    public float particleLightSaturation = 0.7f;
    public float trailChance = 0.2f;
    public float trailSize = 5.0f;
    public int trailFadeTime = 500;
    public FireworksRocketParticle.ParticleGetter<FireworksPath> pathGetter;
    public FireworksRocketParticle.ParticleGetter<Color> colorGetter = (particle, progress, random) -> Color.getHSBColor(random.nextFloat(), 1.0f, 1.0f);
    public float popChance = 0.5f;
    public FireworksExplosion popOptions = simplePopExplosion;
    public FireworksRocketParticle.RandomSoundPlayer explosionSound = (pos, height, random) -> SoundManager.playSound(GameResources.fireworkExplosion, (SoundEffect)SoundEffect.effect(pos.x, pos.y).pitch(random.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue()).volume(Math.max(0.0f, 1.0f - height / 1000.0f) * 1.5f).falloffDistance(2000));

    public FireworksExplosion(FireworksRocketParticle.ParticleGetter<FireworksPath> pathGetter) {
        this.pathGetter = pathGetter;
    }

    public void spawnExplosion(Level level, float startX, float startY, float startHeight, GameRandom random) {
        if (this.explosionSound != null) {
            this.explosionSound.play(new Point2D.Float(startX, startY), startHeight, random);
        }
        for (int i = 0; i <= this.particles; ++i) {
            Trail trail;
            float fParticles = (float)i / (float)this.particles;
            FireworksPath path = this.pathGetter.get(i, fParticles, random);
            Color color = this.colorGetter.get(i, fParticles, random);
            if (random.getChance(this.trailChance)) {
                trail = new Trail(new TrailVector(startX, startY, startX, startY, this.trailSize, startHeight), level, color, this.trailFadeTime);
                level.entityManager.addTrail(trail);
            } else {
                trail = null;
            }
            ParticleOption particle = level.entityManager.addParticle(startX, startY, null).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float progress = path.delta.get(lifeTime, timeAlive, lifePercent);
                pos.x = startX + path.dx * progress;
                pos.y = startY + path.dy * progress;
            }).height((lifeTime, timeAlive, lifePercent) -> {
                float progress = path.delta.get(lifeTime, timeAlive, lifePercent);
                return startHeight + path.dh * progress;
            }).color(color).rotates().sizeFades(this.minSize, this.maxSize).givesLight(this.particleLightHue, this.particleLightSaturation).rotates(200.0f, 300.0f).lifeTime(this.lifetime);
            if (random.getChance(this.popChance) && this.popOptions != null) {
                particle.onProgress(random.getFloatBetween(0.65f, 0.95f), pos -> {
                    float height = particle.getCurrentHeight();
                    this.popOptions.spawnExplosion(level, pos.x, pos.y, height, random);
                });
            }
            if (trail == null) continue;
            particle.onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
                Point2D.Float particlePos = particle.getLevelPos();
                trail.addPoint(new TrailVector(particlePos.x, particlePos.y, startX, startY, this.trailSize * Math.abs(lifePercent - 1.0f), particle.getCurrentHeight()), 0);
            });
        }
    }

    static {
        FireworksExplosion.simplePopExplosion.particles = 1;
        FireworksExplosion.simplePopExplosion.lifetime = 300;
        FireworksExplosion.simplePopExplosion.minSize = 6;
        FireworksExplosion.simplePopExplosion.maxSize = 10;
        FireworksExplosion.simplePopExplosion.trailChance = 0.0f;
        FireworksExplosion.simplePopExplosion.popChance = 0.0f;
        FireworksExplosion.simplePopExplosion.colorGetter = (particle, progress, random) -> ParticleOption.randomFlameColor(random);
        FireworksExplosion.simplePopExplosion.explosionSound = null;
        FireworksExplosion.simplePopExplosion.explosionSound = (pos, height, random) -> {
            if (random.getChance(0.2f)) {
                SoundManager.playSound(GameResources.fireworkCrack, (SoundEffect)SoundEffect.effect(pos.x, pos.y).pitch(random.getOneOf(Float.valueOf(1.0f), Float.valueOf(1.2f), Float.valueOf(1.4f)).floatValue()).volume(Math.max(0.0f, 1.0f - height / 1000.0f) / 4.0f).falloffDistance(2500));
            }
        };
    }
}

