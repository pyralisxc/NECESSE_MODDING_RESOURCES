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
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class EmpressSlashWarningProjectile
extends Projectile {
    public EmpressSlashWarningProjectile() {
    }

    public EmpressSlashWarningProjectile(float x, float y, float angle, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(500);
        this.speed = 500.0f;
    }

    @Override
    public void init() {
        super.init();
        this.piercing = Integer.MAX_VALUE;
        this.canHitMobs = false;
        this.isSolid = false;
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(150, 150, 150), 6.0f, 1000, 18.0f);
        trail.drawOnTop = true;
        return trail;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

