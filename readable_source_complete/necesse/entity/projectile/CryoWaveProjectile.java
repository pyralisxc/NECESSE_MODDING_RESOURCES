/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
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

public class CryoWaveProjectile
extends Projectile {
    public CryoWaveProjectile() {
    }

    public CryoWaveProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    @Override
    public void init() {
        super.init();
        this.piercing = 9999;
        this.setWidth(75.0f);
        this.isSolid = false;
        this.givesLight = true;
        this.particleRandomOffset = 8.0f;
    }

    @Override
    public Color getParticleColor() {
        return new Color(64, 151, 234);
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
        int drawX = camera.getDrawX(this.x) - 64;
        int drawY = camera.getDrawY(this.y) - 64;
        TextureDrawOptionsEnd options = MobRegistry.Textures.cryoQueen.initDraw().sprite(2, 3, 128).light(light).rotate(this.getAngle() - 135.0f, 64, 64).pos(drawX, drawY);
        topList.add(tm -> options.draw());
    }
}

