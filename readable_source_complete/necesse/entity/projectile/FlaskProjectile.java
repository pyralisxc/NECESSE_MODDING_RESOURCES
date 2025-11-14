/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public abstract class FlaskProjectile
extends Projectile {
    public FlaskProjectile() {
    }

    public FlaskProjectile(Level level, Mob owner, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this(level, owner, owner.x, owner.y, targetX, targetY, speed, distance, damage, knockback);
    }

    public FlaskProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setDistance(distance);
        this.setOwner(owner);
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
        this.isSolid = false;
        this.canHitMobs = false;
        this.doesImpactDamage = false;
        this.canBreakObjects = false;
        this.trailOffset = 0.0f;
        this.particleRandomOffset = 1.0f;
        this.particleDirOffset = 0.0f;
    }

    @Override
    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float percDistance = GameMath.limit((float)this.distance / 100.0f, 0.0f, 2.0f) / 2.0f;
        float maxTileHeight = 2.5f;
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float bounceHeight = GameMath.sin(travelPerc * 180.0f);
        float groundHeight = GameMath.lerp(travelPerc, 24.0f, 0.0f);
        this.height = groundHeight + bounceHeight * 32.0f * maxTileHeight * percDistance;
        return out;
    }

    @Override
    public Color getParticleColor() {
        GameLog.debug.println("!! Child of " + FlaskProjectile.class.getSimpleName() + " should override getParticleColor() !!");
        return ThemeColorRegistry.TEST_COLOR.getRandomColor();
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 1;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isClient()) {
            return;
        }
        this.spawnSplashEvent();
    }

    protected abstract void spawnSplashEvent();

    @Override
    protected void spawnDeathParticles() {
        GameLog.debug.println("!! Child of " + FlaskProjectile.class.getSimpleName() + " should override spawnDeathParticles() !!");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        this.angle = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 5.0f;
        if (this.dx < 0.0f) {
            this.angle = -this.angle;
        }
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.angle, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        float shadowAlpha = Math.abs(GameMath.limit(this.height / 250.0f, 0.0f, 1.0f) - 1.0f);
        int shadowX = camera.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
        int shadowY = camera.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
        final TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().light(light).rotate(this.angle).alpha(shadowAlpha).pos(shadowX, shadowY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                shadowOptions.draw();
                options.draw();
            }

            @Override
            public int getSortY() {
                return super.getSortY() + (int)FlaskProjectile.this.height;
            }
        });
    }
}

