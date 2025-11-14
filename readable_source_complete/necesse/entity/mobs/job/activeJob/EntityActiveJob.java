/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.MovedRectangle;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class EntityActiveJob<T extends Entity>
extends ActiveJob {
    public final T target;
    public boolean acceptAdjacentTiles;

    public EntityActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, T target) {
        super(worker, priority);
        this.target = target;
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        Mob mob = this.worker.getMobWorker();
        int directMoveToDistance = this.getDirectMoveToDistance();
        if (directMoveToDistance > 0) {
            Point point = new Point(mob.getX(), mob.getY());
            if (point.distance(((Entity)this.target).x, ((Entity)this.target).y) <= (double)directMoveToDistance && this.hasMoveLOS()) {
                return new JobMoveToTile(this.getDirectMoveTo(this.target));
            }
        }
        return new JobMoveToTile(((Entity)this.target).getTileX(), ((Entity)this.target).getTileY(), this.acceptAdjacentTiles);
    }

    @Override
    public boolean isAt(JobMoveToTile moveToTile) {
        return this.isAtTarget();
    }

    public boolean isAtTarget() {
        return this.getDistanceToTarget() <= (double)this.getCompleteRange() && this.hasLOS();
    }

    public int getCompleteRange() {
        return (int)(GameMath.diagonalDistance * 2.0 * 32.0);
    }

    public double getDistanceToTarget() {
        Mob mob = this.worker.getMobWorker();
        return new Point(mob.getX(), mob.getY()).distance(((Entity)this.target).x, ((Entity)this.target).y);
    }

    public boolean hasLOS() {
        Mob mob = this.worker.getMobWorker();
        Line2D.Float line = new Line2D.Float(mob.x, mob.y, ((Entity)this.target).x, ((Entity)this.target).y);
        return !mob.getLevel().collides(line, this.modifyChasingCollisionFilter(mob, mob.getLevelCollisionFilter()));
    }

    public boolean hasMoveLOS() {
        Mob mob = this.worker.getMobWorker();
        return !mob.getLevel().collides((Shape)new MovedRectangle(mob, ((Entity)this.target).getX(), ((Entity)this.target).getY()), this.modifyChasingCollisionFilter(mob, mob.getLevelCollisionFilter()));
    }

    protected CollisionFilter modifyChasingCollisionFilter(Mob mob, CollisionFilter collisionFilter) {
        if (this.target instanceof Mob) {
            return mob.modifyChasingCollisionFilter(collisionFilter, (Mob)this.target);
        }
        return mob.modifyChasingCollisionFilter(collisionFilter, null);
    }

    public int getDirectMoveToDistance() {
        return 160;
    }

    public abstract MobMovement getDirectMoveTo(T var1);

    @Override
    public boolean isValid(boolean isCurrent) {
        if (this.target == null || this.isInvalidIfTargetRemoved(isCurrent) && ((Entity)this.target).removed() || !((Entity)this.target).isSamePlace(this.worker.getMobWorker())) {
            return false;
        }
        if (!this.worker.getJobRestrictZone().containsTile(((Entity)this.target).getTileX(), ((Entity)this.target).getTileY())) {
            return false;
        }
        return this.isJobValid(isCurrent);
    }

    public abstract boolean isJobValid(boolean var1);

    public boolean isInvalidIfTargetRemoved(boolean isCurrent) {
        return true;
    }

    @Override
    public ActiveJobResult perform() {
        if (!this.isAtTarget()) {
            return ActiveJobResult.MOVE_TO;
        }
        return this.performTarget();
    }

    public abstract ActiveJobResult performTarget();

    public String toString() {
        return super.toString() + "{" + ((Entity)this.target).getUniqueID() + ":" + this.target + "}";
    }
}

