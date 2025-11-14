/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.AscendedFractureGroundEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class AscendedFractureProjectile
extends Projectile {
    private double distBuffer;

    public AscendedFractureProjectile() {
    }

    public AscendedFractureProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    public AscendedFractureProjectile(Level level, float x, float y, float angle, float speed, int distance, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.maxMovePerTick = 32;
        this.height = 0.0f;
        this.isSolid = false;
        this.givesLight = true;
        this.canHitMobs = false;
        this.particleRandomOffset = 10.0f;
        this.particleDirOffset = 0.0f;
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        this.distBuffer += movedDist;
        this.setAngle((float)((double)this.angle + movedDist / 4.0));
        if (this.isServer()) {
            while (this.distBuffer > 32.0) {
                this.distBuffer -= 32.0;
                AscendedFractureGroundEvent event = new AscendedFractureGroundEvent(this.getOwner(), (int)this.x, (int)this.y, this.getDamage(), GameRandom.globalRandom);
                this.getLevel().entityManager.addLevelEvent(event);
            }
        }
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

