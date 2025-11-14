/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EmeraldWandProjectile
extends FollowingProjectile {
    private int lifetime = 5000;
    private final int activationTime = 4000;

    public EmeraldWandProjectile() {
    }

    public EmeraldWandProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
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
        this.spawnTime = this.getWorldEntity().getTime();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.lifetime -= 50;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifetime -= 50;
        if (this.lifetime <= 0) {
            this.remove();
        }
    }

    @Override
    public void updateTarget() {
        if (this.lifetime < 4000) {
            this.findTarget(m -> m.isHostile, 0.0f, 160.0f);
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
        return this.lifetime < 4000;
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
            this.speed = GameMath.limit(this.speed - delta, 5.0f, this.speed);
            this.setDistance(500);
        } else {
            this.speed = 100.0f;
            this.setDistance(1000);
        }
        return super.tickMovement(delta);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public Color getParticleColor() {
        return ThemeColorRegistry.EMERALD.getRandomColor();
    }

    @Override
    protected Color getWallHitColor() {
        return ThemeColorRegistry.EMERALD.getRandomColor();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getWorldEntity().getTime() - this.spawnTime, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.emeraldWand).volume(0.18f);
    }
}

