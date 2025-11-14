/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public class IgnoreFailureActiveJob
extends ActiveJob {
    public final ActiveJob job;

    public IgnoreFailureActiveJob(ActiveJob job) {
        super(job.worker, job.priority);
        this.job = job;
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        return this.job.getMoveToTile(lastTile);
    }

    @Override
    public boolean isAt(JobMoveToTile moveToTile) {
        return this.job.isAt(moveToTile);
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        this.job.tick(isCurrent, isMovingTo);
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        return this.job.isValid(isCurrent);
    }

    @Override
    public void onCancelled(boolean becauseOfInvalid, boolean isCurrent, boolean isMovingTo) {
        this.job.onCancelled(becauseOfInvalid, isCurrent, isMovingTo);
    }

    @Override
    public ActiveJobResult perform() {
        ActiveJobResult result = this.job.perform();
        if (result == ActiveJobResult.FAILED || result == null) {
            return ActiveJobResult.FINISHED;
        }
        return result;
    }
}

