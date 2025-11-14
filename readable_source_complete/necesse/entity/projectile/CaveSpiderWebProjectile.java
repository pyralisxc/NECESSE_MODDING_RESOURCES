/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderWebEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class CaveSpiderWebProjectile
extends Projectile {
    public CaveSpiderWebProjectile() {
    }

    public CaveSpiderWebProjectile(float x, float y, float targetX, float targetY, GameDamage damage, Mob owner, int distance) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = 80.0f;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.height = 16.0f;
        this.canHitMobs = false;
    }

    @Override
    public Color getParticleColor() {
        return new Color(220, 215, 210);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 16.0f, 200, this.getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        Mob owner = this.getOwner();
        if (owner != null && !owner.removed()) {
            CaveSpiderWebEvent event = new CaveSpiderWebEvent(owner, (int)x, (int)y, GameRandom.globalRandom);
            this.getLevel().entityManager.events.add(event);
        }
    }
}

