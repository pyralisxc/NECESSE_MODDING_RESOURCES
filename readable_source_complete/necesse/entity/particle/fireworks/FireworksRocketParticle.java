/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle.fireworks;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksExplosion;
import necesse.entity.particle.fireworks.FireworksPath;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class FireworksRocketParticle
extends Particle {
    public GameRandom random;
    protected float currentHeight;
    public float heightTrajectory = 0.5f;
    public float height;
    public FireworksExplosion explosion;

    public FireworksRocketParticle(Level level, float x, float y, long lifeTime, int height, FireworksExplosion explosion, GameRandom random) {
        super(level, x, y, lifeTime);
        this.explosion = explosion;
        this.height = height;
        this.random = random;
        this.dx = random.floatGaussian() * 10.0f;
        this.dy = random.floatGaussian() * 10.0f;
        this.friction = 0.3f;
    }

    public FireworksRocketParticle(Level level, float x, float y, long lifeTime, int height, ParticleGetter<FireworksPath> pathGetter, GameRandom random) {
        this(level, x, y, lifeTime, height, new FireworksExplosion(pathGetter), random);
    }

    @Override
    public void init() {
        super.init();
        SoundManager.playSound(GameResources.fireworkFuse, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(this.random.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue()).volume(0.7f));
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.currentHeight = (float)Math.pow(this.getLifeCyclePercent(), this.heightTrajectory) * this.height;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().entityManager.addParticle(this.x, this.y, null).height(this.currentHeight).flameColor().givesLight(ParticleOption.defaultFlameHue, 0.7f);
    }

    @Override
    public void remove() {
        if (!this.removed()) {
            this.explosion.spawnExplosion(this.getLevel(), this.x, this.y, this.currentHeight, this.random);
        }
        super.remove();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public static interface ParticleGetter<T> {
        public T get(int var1, float var2, GameRandom var3);
    }

    public static interface ExplosionModifier {
        public void play(FireworksExplosion var1, float var2, GameRandom var3);
    }

    public static interface RandomSoundPlayer {
        public void play(Point2D.Float var1, float var2, GameRandom var3);
    }
}

