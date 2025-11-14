/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public class SingleJobSequence
implements JobSequence {
    private ActiveJob job;
    public GameMessage activityDescription;

    public SingleJobSequence(ActiveJob job, GameMessage activityDescription) {
        this.job = job;
        this.activityDescription = activityDescription;
    }

    @Override
    public GameMessage getActivityDescription() {
        return this.activityDescription;
    }

    @Override
    public void setActivityDescription(GameMessage description) {
        this.activityDescription = description;
    }

    @Override
    public void tick() {
        if (this.job != null) {
            this.job.tick(false, false);
        }
    }

    @Override
    public boolean hasNext() {
        return this.job != null;
    }

    @Override
    public ActiveJob next() {
        ActiveJob temp = this.job;
        this.job = null;
        return temp;
    }

    @Override
    public boolean isValid() {
        return this.job == null || this.job.isValid(false);
    }

    @Override
    public ActiveJobTargetFoundResult onTargetFound(Mob target) {
        if (this.job == null) {
            return ActiveJobTargetFoundResult.CONTINUE;
        }
        return this.job.onTargetFound(target, false, false);
    }

    @Override
    public void cancel(boolean becauseOfInvalid) {
        if (this.job != null) {
            this.job.onCancelled(becauseOfInvalid, false, false);
        }
    }
}

