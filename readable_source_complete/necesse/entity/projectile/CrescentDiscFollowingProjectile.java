/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CrescentDiscFollowingProjectile
extends FollowingProjectile {
    private final float fadeDistance = 0.1f;
    private float lastAngle;

    public CrescentDiscFollowingProjectile() {
    }

    public CrescentDiscFollowingProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(70.0f, 70.0f);
        this.turnSpeed = 5.0f;
        this.height = 0.0f;
        this.trailOffset = 0.0f;
        this.lastAngle = 0.0f;
        this.isSolid = false;
        this.givesLight = true;
        this.piercing = 2;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        GameRandom random = GameRandom.globalRandom;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
        float distance = 90.0f;
        this.getLevel().entityManager.addParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(255, 255, 255)).givesLight(247.0f, 0.3f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(-delta * 10.0f / 250.0f), Float::sum).floatValue();
            pos.x = this.x + GameMath.sin(angle) * distance;
            pos.y = this.y + GameMath.cos(angle) * distance;
        }).lifeTime(1000).sizeFades(16, 24);
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        this.lastAngle += 5.0f * this.getIntensity();
    }

    @Override
    protected void spawnSpinningParticle() {
    }

    @Override
    public Color getParticleColor() {
        return new Color(220, 212, 255);
    }

    @Override
    protected void replaceTrail() {
        super.replaceTrail();
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void updateTarget() {
        if (this.traveledDistance > 50.0f) {
            this.findTarget(m -> m.isPlayer, 0.0f, 1000.0f);
        }
    }

    @Override
    public boolean canHit(Mob mob) {
        return this.getIntensity() == 1.0f && super.canHit(mob);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.lastAngle + this.getIntensity(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight()).alpha(this.getIntensity());
        topList.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    private float getIntensity() {
        if (this.traveledDistance < (float)this.distance * 0.1f) {
            return this.traveledDistance / ((float)this.distance * 0.1f);
        }
        if ((float)this.distance - this.traveledDistance < (float)this.distance * 0.1f) {
            return ((float)this.distance - this.traveledDistance) / ((float)this.distance * 0.1f);
        }
        return 1.0f;
    }
}

