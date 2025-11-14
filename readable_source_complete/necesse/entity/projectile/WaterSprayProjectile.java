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

public class WaterSprayProjectile
extends Projectile {
    @Override
    public void init() {
        super.init();
        this.maxMovePerTick = 24;
        this.height = 18.0f;
        this.piercing = 1;
        this.bouncing = 0;
    }

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public Color getParticleColor() {
        return new Color(55, 125, 216);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0f, 250, this.getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

