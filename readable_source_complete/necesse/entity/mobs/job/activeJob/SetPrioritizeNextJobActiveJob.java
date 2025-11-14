/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import necesse.engine.registries.LevelJobRegistry;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.LevelJob;

public class SetPrioritizeNextJobActiveJob
extends ActiveJob {
    public int jobID;
    public boolean resetPrioritizeNextJobIfFound;

    public SetPrioritizeNextJobActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, int jobID, boolean resetPrioritizeNextJobIfFound) {
        super(worker, priority);
        this.jobID = jobID;
        this.resetPrioritizeNextJobIfFound = resetPrioritizeNextJobIfFound;
    }

    public SetPrioritizeNextJobActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, Class<? extends LevelJob> job, boolean resetPrioritizeNextJobIfFound) {
        this(worker, priority, LevelJobRegistry.getJobID(job), resetPrioritizeNextJobIfFound);
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        return null;
    }

    @Override
    public boolean isAt(JobMoveToTile moveToTile) {
        return true;
    }

    @Override
    public void onMadeCurrent() {
        super.onMadeCurrent();
        this.worker.setPrioritizeNextJob(this.jobID, this.resetPrioritizeNextJobIfFound);
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        return true;
    }

    @Override
    public ActiveJobResult perform() {
        return ActiveJobResult.FINISHED;
    }
}

