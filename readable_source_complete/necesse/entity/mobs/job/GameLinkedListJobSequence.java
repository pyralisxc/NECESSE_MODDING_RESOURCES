/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public class GameLinkedListJobSequence
extends GameLinkedList<ActiveJob>
implements JobSequence {
    public GameMessage activityDescription;

    public GameLinkedListJobSequence(GameMessage activityDescription) {
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
        for (ActiveJob activeJob : this) {
            activeJob.tick(false, false);
        }
    }

    @Override
    public boolean hasNext() {
        return !this.isEmpty();
    }

    @Override
    public ActiveJob next() {
        return (ActiveJob)this.removeFirst();
    }

    @Override
    public boolean isValid() {
        if (this.isEmpty()) {
            return true;
        }
        GameLinkedList.Element current = this.getFirstElement();
        while (current != null) {
            GameLinkedList.Element next = current.next();
            if (!((ActiveJob)current.object).isValid(false)) {
                if (((ActiveJob)current.object).shouldClearSequence()) {
                    return false;
                }
                current.remove();
            }
            current = next;
        }
        return true;
    }

    @Override
    public ActiveJobTargetFoundResult onTargetFound(Mob target) {
        for (ActiveJob activeJob : this) {
            ActiveJobTargetFoundResult next = activeJob.onTargetFound(target, false, false);
            if (next != ActiveJobTargetFoundResult.FAIL) continue;
            return next;
        }
        return ActiveJobTargetFoundResult.CONTINUE;
    }

    @Override
    public void cancel(boolean becauseOfInvalid) {
        for (ActiveJob activeJob : this) {
            activeJob.onCancelled(becauseOfInvalid, false, false);
        }
    }
}

