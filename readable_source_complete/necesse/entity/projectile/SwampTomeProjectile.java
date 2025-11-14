/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.boomerangProjectile.SpinningProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampTomeProjectile
extends SpinningProjectile {
    public SwampTomeProjectile() {
    }

    public SwampTomeProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.setWidth(12.0f);
        this.bouncing = 15;
        this.piercing = 3;
        this.givesLight = true;
        this.trailOffset = 0.0f;
    }

    @Override
    public Color getParticleColor() {
        return new Color(31, 112, 57);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 16.0f, 300, this.getHeight());
    }

    @Override
    protected float getSpinningSpeed() {
        return 0.1f;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        long lifeTime = this.getWorldEntity().getTime() - this.spawnTime;
        float pulse = GameUtils.getAnimFloatContinuous(lifeTime, 500);
        int dimX = (int)((float)this.texture.getWidth() * (pulse / 3.0f + 1.0f));
        int dimY = (int)((float)this.texture.getHeight() * (pulse / 3.0f + 1.0f));
        int drawX = camera.getDrawX(this.x) - dimX / 2;
        int drawY = camera.getDrawY(this.y) - dimY / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).size(dimX, dimY).rotate(this.getAngle(), dimX / 2, dimY / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), dimY / 2);
    }
}

