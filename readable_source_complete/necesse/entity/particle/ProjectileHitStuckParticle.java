/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public abstract class ProjectileHitStuckParticle
extends Particle {
    protected Mob target;
    protected float xOffset;
    protected float yOffset;
    protected float angle;

    public ProjectileHitStuckParticle(Mob target, Projectile projectile, float x, float y, float insertDistance, long lifeTime) {
        super(projectile.getLevel(), x, y, lifeTime);
        this.target = target;
        this.xOffset = x - (target == null ? 0.0f : target.x);
        this.yOffset = y - (target == null ? 0.0f : target.y);
        Point2D.Float normalize = GameMath.normalize(projectile.dx, projectile.dy);
        this.xOffset += normalize.x * insertDistance;
        this.yOffset += normalize.y * insertDistance;
        this.angle = projectile.getAngle();
    }

    private float levelDrawX() {
        if (this.target == null) {
            return this.xOffset;
        }
        return (float)this.target.getDrawX() + this.xOffset;
    }

    private float levelDrawY() {
        if (this.target == null) {
            return this.yOffset;
        }
        return (float)this.target.getDrawY() + this.yOffset;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.target != null && this.target.removed() || this.removed()) {
            this.remove();
            return;
        }
        this.x = this.levelDrawX();
        this.y = this.levelDrawY();
        this.addDrawables(this.target, this.x, this.y, this.angle, list, tileList, topList, level, tickManager, camera, perspective);
    }

    public abstract void addDrawables(Mob var1, float var2, float var3, float var4, List<LevelSortedDrawable> var5, OrderableDrawables var6, OrderableDrawables var7, Level var8, TickManager var9, GameCamera var10, PlayerMob var11);
}

