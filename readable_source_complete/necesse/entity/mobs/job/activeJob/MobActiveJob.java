/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.EntityActiveJob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public abstract class MobActiveJob<T extends Mob>
extends EntityActiveJob<T> {
    public MobActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, T target) {
        super(worker, priority, target);
    }

    @Override
    public MobMovement getDirectMoveTo(Mob target) {
        return new MobMovementRelative(target, 0.0f, 0.0f);
    }

    public boolean hasProjectileLOS() {
        return this.hasProjectileLOS(((Mob)this.target).x, ((Mob)this.target).y);
    }

    public boolean hasProjectileLOS(float targetPosX, float targetPosY) {
        Mob mob;
        Line2D.Float line = new Line2D.Float(mob.x, mob.y, targetPosX, targetPosY);
        mob = this.worker.getMobWorker();
        Mob hit = GameUtils.castRayFirstHit(line, 100.0, checkLine -> {
            Stream<Mob> mobStream = this.getLevel().entityManager.mobs.streamInRegionsShape((Shape)checkLine, 1);
            Stream playersStream = this.getLevel().entityManager.players.streamInRegionsShape((Shape)checkLine, 1);
            return Stream.concat(mobStream, playersStream).filter(m -> m != mob).filter(m -> {
                Rectangle collision = m.getCollision();
                return checkLine.intersects(collision) || GameMath.getPerpendicularLine(checkLine, 10.0f).intersects(collision) || GameMath.getPerpendicularLine(checkLine, -10.0f).intersects(collision);
            }).min(Comparator.comparingDouble(mob::getDistance)).orElse(null);
        });
        return hit == null || hit == this.target;
    }
}

