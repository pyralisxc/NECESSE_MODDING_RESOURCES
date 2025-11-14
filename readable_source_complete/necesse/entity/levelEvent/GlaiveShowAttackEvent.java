/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.AttackAnimMob;

public abstract class GlaiveShowAttackEvent
extends LevelEvent {
    protected float lastAngleTick;
    protected float anglePerTick;
    protected int aimX;
    protected int aimY;
    protected int attackSeed;
    protected AttackAnimMob attackMob;

    public GlaiveShowAttackEvent(AttackAnimMob attackMob, int x, int y, int attackSeed, float anglePerTick) {
        super(false);
        this.attackMob = attackMob;
        this.aimX = x - attackMob.getX();
        this.aimY = y - attackMob.getY();
        this.attackSeed = attackSeed;
        this.anglePerTick = anglePerTick;
    }

    @Override
    public void init() {
        super.init();
        if (this.attackMob == null) {
            this.over();
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.isOver()) {
            return;
        }
        if (!this.attackMob.isAttacking || this.attackMob.attackSeed != this.attackSeed) {
            this.over();
            return;
        }
        float nextProgress = this.attackMob.getAttackAnimProgress();
        float nextAngle = nextProgress * 360.0f;
        if (nextAngle >= this.lastAngleTick + this.anglePerTick) {
            for (float i = this.lastAngleTick + this.anglePerTick; i <= nextAngle; i += this.anglePerTick) {
                this.tick(i);
                this.lastAngleTick = i;
            }
        }
    }

    public abstract void tick(float var1);

    public Point2D.Float getAngleDir(float angle) {
        if (this.attackMob != null && this.attackMob.getDir() == 3) {
            return GameMath.getAngleDir(-angle - 110.0f);
        }
        return GameMath.getAngleDir(angle + 110.0f);
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        if (this.attackMob != null) {
            return this.attackMob.getRegionPositions();
        }
        return super.getRegionPositions();
    }

    @Override
    public Point getSaveToRegionPos() {
        if (this.attackMob != null) {
            return new Point(this.level.regionManager.getRegionCoordByTile(this.attackMob.getTileX()), this.level.regionManager.getRegionCoordByTile(this.attackMob.getTileY()));
        }
        return super.getSaveToRegionPos();
    }
}

