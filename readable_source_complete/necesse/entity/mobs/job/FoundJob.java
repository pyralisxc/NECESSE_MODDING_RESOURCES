/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.level.maps.levelData.jobs.LevelJob;

public class FoundJob<T extends LevelJob> {
    public final EntityJobWorker worker;
    public final T job;
    public final JobTypeHandler.SubHandler<T> handler;
    public final JobTypeHandler.TypePriority priority;
    public final ComputedValue<Object> preSequenceCompute;
    private double workerDistance = -1.0;
    private boolean canMoveToComputed;
    private boolean canMoveTo;
    private boolean sequenceComputed;
    private JobSequence sequence;

    public FoundJob(EntityJobWorker worker, T job, JobTypeHandler.SubHandler<T> handler, JobTypeHandler.TypePriority priority, ComputedValue<Object> preSequenceCompute) {
        this.worker = worker;
        this.job = job;
        this.handler = handler;
        this.priority = priority;
        this.preSequenceCompute = preSequenceCompute;
    }

    public FoundJob(EntityJobWorker worker, T job, JobTypeHandler handler, ComputedValue<Object> preSequenceCompute) {
        this.worker = worker;
        this.job = job;
        this.handler = handler.getJobHandler(((LevelJob)job).getID());
        this.priority = this.handler.priority;
        this.preSequenceCompute = preSequenceCompute;
    }

    public double getDistanceFromWorker() {
        if (this.workerDistance < 0.0) {
            Mob mob = this.worker.getMobWorker();
            this.workerDistance = GameMath.diagonalMoveDistance(mob.getX(), mob.getY(), ((LevelJob)this.job).tileX * 32 + 16, ((LevelJob)this.job).tileY * 32 + 16);
        }
        return this.workerDistance;
    }

    public boolean canMoveTo() {
        if (!this.canMoveToComputed) {
            this.canMoveTo = this.worker.estimateCanMoveTo(((LevelJob)this.job).tileX, ((LevelJob)this.job).tileY, true);
            this.canMoveToComputed = true;
        }
        return this.canMoveTo;
    }

    public JobSequence getSequence() {
        if (!this.sequenceComputed) {
            Mob mob = this.worker.getMobWorker();
            Performance.record((PerformanceTimerManager)mob.getLevel().tickManager(), "getSequence", () -> Performance.record((PerformanceTimerManager)mob.getLevel().tickManager(), ((LevelJob)this.job).getStringID(), () -> {
                this.sequence = this.handler.sequenceFunction.apply(this);
                this.sequenceComputed = true;
            }));
        }
        return this.sequence;
    }
}

