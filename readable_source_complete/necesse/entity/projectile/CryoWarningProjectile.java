/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class CryoWarningProjectile
extends Projectile {
    public CryoWarningProjectile() {
    }

    public CryoWarningProjectile(float x, float y, float angle, float speed, int distance) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.speed = speed;
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.canHitMobs = false;
        this.isSolid = false;
        this.height = 0.0f;
    }

    @Override
    public Color getParticleColor() {
        return new Color(64, 151, 234);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(64, 151, 234), 28.0f, 1000, this.getHeight());
        trail.drawOnTop = true;
        return trail;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

