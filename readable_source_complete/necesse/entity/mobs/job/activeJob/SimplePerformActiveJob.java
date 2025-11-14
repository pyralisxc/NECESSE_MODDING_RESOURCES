/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class SimplePerformActiveJob
extends ActiveJob {
    public SimplePerformActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority) {
        super(worker, priority);
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
    public void tick(boolean isCurrent, boolean isMovingTo) {
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        return true;
    }
}

