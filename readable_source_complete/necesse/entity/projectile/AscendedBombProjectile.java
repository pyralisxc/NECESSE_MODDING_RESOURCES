/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.levelEvent.explosionEvent.AscendedBombExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class AscendedBombProjectile
extends FollowingProjectile {
    private int lifetime = 20000;
    private final int activationTime = 18000;

    public AscendedBombProjectile() {
    }

    public AscendedBombProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
        this.bouncing = 3;
        this.givesLight = true;
        this.lightSaturation = 171.0f;
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(40.0f, true);
        this.turnSpeed = 50.0f;
        this.particleSpeedMod = 0.03f;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.lifetime -= 50;
        if (this.target == null) {
            this.setAngle(this.angle + 10.0f);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifetime -= 50;
        if (this.lifetime <= 0) {
            this.remove();
        }
        if (this.target == null) {
            this.setAngle(this.angle + 10.0f);
        }
    }

    @Override
    public void updateTarget() {
        if (this.lifetime < 18000) {
            this.findTarget(m -> m.isHostile, 0.0f, 160.0f);
        }
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            this.getLevel().entityManager.addLevelEvent(new AscendedBombExplosionEvent(x, y, 100, new GameDamage(100.0f), true, 10.0f, this.getOwner()));
        }
    }

    @Override
    public void findTarget(Predicate<Mob> filter, float frontOffset, float maxDistance) {
        this.target = null;
        int targetX = (int)(this.x + this.dx * frontOffset);
        int targetY = (int)(this.y + this.dy * frontOffset);
        ComputedObjectValue nextTarget = GameUtils.streamTargetsRange(this.getOwner(), targetX, targetY, (int)maxDistance).filter(m -> m != null && !m.removed()).filter(filter).map(m -> new ComputedObjectValue<Mob, Double>((Mob)m, () -> m.getPositionPoint().distance(targetX, targetY))).min(Comparator.comparing(ComputedValue::get)).orElse(null);
        if (nextTarget != null && (Double)nextTarget.get() <= (double)maxDistance) {
            this.target = (Entity)nextTarget.object;
        }
    }

    @Override
    public boolean canHit(Mob mob) {
        return this.lifetime < 18000;
    }

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public float tickMovement(float delta) {
        if (this.target == null) {
            this.speed = GameMath.limit(this.speed - delta, 15.0f, this.speed);
            this.setDistance(1000);
        } else {
            this.speed = 50.0f;
            this.setDistance(2000);
        }
        return super.tickMovement(delta);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(255, 0, 231), 12.0f, 200, this.getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int centerDistance = this.texture.getHeight() / 2;
        int drawX = camera.getDrawX(this.x) - centerDistance;
        int drawY = camera.getDrawY(this.y) - centerDistance;
        int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(anim, 0, 36).light(light.minLevelCopy(150.0f)).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().light(light.minLevelCopy(150.0f)).pos(drawX - 2, drawY - 2);
        tileList.add(tm -> shadowOptions.draw());
    }
}

