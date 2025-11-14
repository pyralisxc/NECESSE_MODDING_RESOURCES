/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.followingProjectile;

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

public class AncestorMageProjectile
extends Projectile {
    public AncestorMageProjectile() {
    }

    public AncestorMageProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    @Override
    public void init() {
        super.init();
        this.givesLight = true;
        this.height = 0.0f;
        this.piercing = 1;
        this.setWidth(30.0f);
    }

    @Override
    protected void replaceTrail() {
    }

    @Override
    public Color getParticleColor() {
        return new Color(0, 222, 218);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(0, 222, 218), 12.0f, 200, 0.0f);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.spawnSpinningParticle();
    }

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

