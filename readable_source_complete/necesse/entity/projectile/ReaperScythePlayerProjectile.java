/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperScythePlayerProjectile
extends Projectile {
    public ReaperScythePlayerProjectile() {
    }

    public ReaperScythePlayerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this();
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
        this.setWidth(90.0f, true);
        this.isCircularHitbox = true;
        this.isSolid = false;
        this.height = 18.0f;
        this.piercing = 2;
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
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 48;
        int drawY = camera.getDrawY(this.y) - 48;
        int height = (int)this.getHeight();
        boolean mirror = this.dx < 0.0f;
        float angle = this.getAngle() * (float)(mirror ? -1 : 1);
        TextureDrawOptionsEnd options = this.texture.initDraw().sprite(0, 0, 96).mirror(false, !mirror).light(light).rotate(angle, 48, 48);
        int minLight = 100;
        TextureDrawOptionsEnd glow = this.texture.initDraw().sprite(0, 1, 96).mirror(false, !mirror).light(light.minLevelCopy(minLight)).alpha(0.6f).rotate(angle, 48, 48).pos(drawX, drawY - height);
        TextureDrawOptionsEnd main = options.copy().pos(drawX, drawY - height);
        TextureDrawOptionsEnd shadow1 = options.copy().alpha(0.6f).rotate(angle + (float)(mirror ? 60 : -60), 48, 48).pos(drawX, drawY - height);
        TextureDrawOptionsEnd shadow2 = options.copy().alpha(0.3f).rotate(angle + (float)(mirror ? 120 : -120), 48, 48).pos(drawX, drawY - height);
        topList.add(tm -> {
            shadow2.draw();
            shadow1.draw();
            main.draw();
            glow.draw();
        });
    }

    @Override
    public float getAngle() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }
}

