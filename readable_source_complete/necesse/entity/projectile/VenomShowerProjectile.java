/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class VenomShowerProjectile
extends Projectile {
    private boolean hitMob = false;

    public VenomShowerProjectile() {
    }

    public VenomShowerProjectile(Level level, float x, float y, float targetX, float targetY, int distance, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = (float)distance * 1.1f;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.canBounce = false;
        this.height = 16.0f;
        this.piercing = 2;
        this.setWidth(25.0f);
        if (this.isClient()) {
            int amount = this.distance / 6;
            this.spawnSprayParticles(amount);
        }
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        float progress = this.traveledDistance / (float)this.distance;
        this.speed = GameMath.lerp(progress, (float)this.distance * 1.1f, 5.0f);
    }

    public void spawnSprayParticles(int amount) {
        ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        for (int i = 0; i < amount; ++i) {
            float posX = this.x + GameRandom.globalRandom.floatGaussian() * 8.0f;
            float posY = this.y + GameRandom.globalRandom.floatGaussian() * 8.0f;
            float projectileHeight = this.getHeight();
            float startHeight = GameRandom.globalRandom.getFloatBetween(projectileHeight - 2.0f, projectileHeight + 4.0f);
            float startHeightSpeed = GameRandom.globalRandom.getFloatBetween(0.0f, 60.0f);
            float endHeight = GameRandom.globalRandom.getFloatBetween(-10.0f, -5.0f);
            float gravity = GameRandom.globalRandom.getFloatBetween(8.0f, 20.0f);
            float distanceLeft = (float)this.distance - this.traveledDistance;
            float floatPower = GameRandom.globalRandom.getFloatBetween(0.1f, 1.0f);
            float power = floatPower * (distanceLeft + 50.0f);
            float friction = 1.0f;
            int lifeAdded = (int)(250.0f * floatPower);
            int timeToLive = GameRandom.globalRandom.getIntBetween(250 + lifeAdded, 750 + lifeAdded);
            int timeToFadeOut = GameRandom.globalRandom.getIntBetween(500, 1000);
            int totalTime = timeToLive + timeToFadeOut;
            ParticleOption.HeightMover heightMover = new ParticleOption.HeightMover(startHeight, startHeightSpeed, gravity, 2.0f, endHeight, 0.0f);
            ParticleOption.FrictionMover frictionMover = new ParticleOption.FrictionMover(this.dx * power, this.dy * power, friction);
            ParticleOption.CollisionMover mover = new ParticleOption.CollisionMover(this.getLevel(), frictionMover);
            this.getLevel().entityManager.addParticle(posX, posY, particleTypeSwitcher.next()).fadesAlphaTime(0, timeToFadeOut).color(new Color(20, 120, 50)).sizeFadesInAndOut(10, 15, 100, 0).height(heightMover).rotates().moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                if (!(!(heightMover.currentHeight > endHeight) || this.removed() && this.hitMob)) {
                    mover.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                }
            }).givesLight(110.0f, 0.7f).lifeTime(totalTime);
        }
    }

    @Override
    public GameDamage getDamage() {
        float progress = this.traveledDistance / (float)this.distance;
        float modifier = GameMath.lerp(progress, 1.2f, 0.2f);
        return super.getDamage().modFinalMultiplier(modifier);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        this.hitMob = mob != null;
    }

    @Override
    public Color getParticleColor() {
        return null;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected Color getWallHitColor() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

