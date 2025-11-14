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
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class CrystalShieldRetaliationProjectile
extends Projectile {
    private boolean hitMob = false;

    public CrystalShieldRetaliationProjectile() {
    }

    public CrystalShieldRetaliationProjectile(Level level, float x, float y, float targetX, float targetY, int distance, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = (float)distance * 1.1f;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
        this.canBounce = false;
    }

    @Override
    public void init() {
        super.init();
        this.height = 16.0f;
        this.piercing = 2;
        this.setWidth(100.0f);
        if (this.isClient()) {
            int amount = this.distance / 3;
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
            float posX = this.x + GameRandom.globalRandom.floatGaussian() * 30.0f;
            float posY = this.y + GameRandom.globalRandom.floatGaussian() * 30.0f;
            float projectileHeight = this.getHeight();
            float startHeight = GameRandom.globalRandom.getFloatBetween(projectileHeight - 2.0f, projectileHeight + 4.0f);
            float startHeightSpeed = GameRandom.globalRandom.getFloatBetween(0.0f, 60.0f);
            float endHeight = GameRandom.globalRandom.getFloatBetween(-10.0f, -5.0f);
            float gravity = GameRandom.globalRandom.getFloatBetween(16.0f, 40.0f);
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
            float hueMod = (float)this.getLevel().getWorldEntity().getLocalTime() / 10.0f % 240.0f;
            float glowHue = hueMod < 120.0f ? hueMod + 200.0f : 440.0f - hueMod;
            Color color1 = new Color(198, 236, 255);
            Color color2 = new Color(248, 198, 218);
            Color color3 = new Color(184, 174, 255);
            Color color4 = new Color(193, 120, 170);
            Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3, color4);
            this.getLevel().entityManager.addParticle(posX, posY, particleTypeSwitcher.next()).fadesAlphaTime(0, timeToFadeOut).color(color).sizeFadesInAndOut(11, 22, 100, 0).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).height(heightMover).movesFriction(this.dx * power, this.dy * power, 0.95f).ignoreLight(true).givesLight(glowHue, 1.0f).lifeTime(totalTime);
        }
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

