/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class SmiteBeamProjectile
extends Projectile {
    protected float nextTrailUpdatePoint = 0.0f;
    protected float initialX;
    protected float targetX;
    protected float targetY;
    protected Mob targetMob;

    public SmiteBeamProjectile() {
    }

    protected SmiteBeamProjectile(Level level, Mob owner, GameDamage damage) {
        this.setLevel(level);
        this.setOwner(owner);
        this.speed = 1500.0f;
        this.knockback = 0;
        this.setDamage(damage);
    }

    public SmiteBeamProjectile(Level level, Mob owner, float startX, float startY, float targetX, float targetY, GameDamage damage) {
        this(level, owner, damage);
        this.targetX = targetX;
        this.targetY = targetY;
        this.setStartPos(startX, startY, targetX, targetY);
        this.setTarget(targetX, targetY);
    }

    public SmiteBeamProjectile(Level level, Mob owner, float targetX, float targetY, GameDamage damage) {
        this(level, owner, targetX, targetY, targetX, targetY, damage);
    }

    public SmiteBeamProjectile(Level level, Mob owner, float startX, float startY, Mob targetMob, GameDamage damage) {
        this(level, owner, damage);
        this.targetMob = targetMob;
        Objects.requireNonNull(targetMob);
        this.setStartPos(startX, startY, targetMob.x, targetMob.y);
    }

    public SmiteBeamProjectile(Level level, Mob owner, Mob targetMob, GameDamage damage) {
        this(level, owner, targetMob.x, targetMob.y, targetMob, damage);
    }

    protected void setStartPos(float levelX, float levelY, float initialTargetX, float initialTargetY) {
        this.x = levelX;
        this.y = levelY - 1000.0f;
        this.distance = (int)GameMath.getExactDistance(this.x, this.y, initialTargetX, initialTargetY);
    }

    @Override
    public void init() {
        super.init();
        this.height = 40.0f;
        this.piercing = 999;
        this.isSolid = false;
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0f;
        this.removeIfOutOfBounds = false;
        this.canBreakObjects = false;
        this.setWidth(6.0f, false);
        this.initialX = this.x;
        if (this.targetMob != null) {
            this.updateTargetMobPos();
        }
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        GameRandom random = new GameRandom();
        if (this.traveledDistance > (float)this.distance * 0.75f && this.x < this.initialX) {
            this.x += (float)random.getIntBetween(1, 2);
        } else if (this.traveledDistance > (float)this.distance * 0.75f && this.x > this.initialX) {
            this.x -= (float)random.getIntBetween(1, 2);
        } else if (this.traveledDistance > this.nextTrailUpdatePoint) {
            float randomAdditionToNextPoint = random.getFloatBetween((float)this.distance * 0.01f, (float)this.distance * 0.02f);
            this.nextTrailUpdatePoint += randomAdditionToNextPoint;
            float rndX = random.getIntBetween(-6, 6);
            this.x += rndX;
        }
        if (this.targetMob != null) {
            this.updateTargetMobPos();
        }
    }

    protected void updateTargetMobPos() {
        if (this.targetMob != null) {
            this.setTarget(this.targetMob.x, this.targetMob.y);
        } else {
            this.setTarget(this.x, this.y - 1000.0f);
        }
    }

    @Override
    public Color getParticleColor() {
        return ThemeColorRegistry.LIGHTNING.getRandomColor();
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), this.getParticleColor(), 14.0f, 750, this.getHeight());
        trail.drawOnTop = true;
        trail.lightHue = 60.0f;
        trail.lightLevel = 120;
        return trail;
    }

    @Override
    public float getTrailThickness() {
        float v = ((float)this.distance - this.traveledDistance) / 16.0f;
        return GameMath.limit(v, 3.0f, 10.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    protected void spawnWallHitParticles(float x, float y) {
    }

    @Override
    protected void spawnSpinningParticle() {
    }

    @Override
    public void spawnTrailParticle(Point2D.Float startPos, double movedDist, Color color, float countDiv, int lifeTime) {
    }

    @Override
    protected void spawnDeathParticles() {
        for (int i = 0; i <= 8; ++i) {
            float anglePerParticle = 45.0f;
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle));
            float dy = (float)Math.cos(Math.toRadians(angle));
            float speedMultiplierX = GameRandom.globalRandom.getFloatBetween(-12.0f, 12.0f) * 0.9f;
            float speedMultiplierY = GameRandom.globalRandom.getFloatBetween(-6.0f, 6.0f) * 0.9f;
            this.getLevel().entityManager.addParticle(this.x, this.y + 10.0f, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.particles.sprite(0, 0, 8)).sizeFades(4, 16).movesConstant(dx * speedMultiplierX, dy * speedMultiplierY).height(10.0f).color(ThemeColorRegistry.LIGHTNING.getRandomColor()).givesLight(60.0f, 0.7f).lifeTime(1000);
        }
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.fizz, (SoundEffect)SoundEffect.effect(x, y).volume(0.35f).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
        }
    }
}

