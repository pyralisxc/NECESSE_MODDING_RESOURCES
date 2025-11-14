/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.MobActiveJob;

public class TestMobActiveJob
extends MobActiveJob<Mob> {
    private int work;
    private int totalWork;

    public TestMobActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, Mob target, int totalWork) {
        super(worker, priority, target);
        this.totalWork = totalWork;
    }

    @Override
    public boolean isJobValid(boolean isCurrent) {
        return true;
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
    }

    @Override
    public ActiveJobResult performTarget() {
        if (this.work % 20 == 0) {
            System.out.println(this.worker.getMobWorker().getDisplayName() + " working " + this.work);
        }
        if (!this.worker.isInWorkAnimation()) {
            this.worker.showPlaceAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), null, 250);
        }
        ++this.work;
        if (this.work < this.totalWork) {
            return ActiveJobResult.PERFORMING;
        }
        System.out.println(this.worker.getMobWorker().getDisplayName() + " finished working " + this.work);
        return ActiveJobResult.FINISHED;
    }
}

