/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public class LinkedListJobSequence
extends LinkedList<ActiveJob>
implements JobSequence {
    public GameMessage activityDescription;

    public LinkedListJobSequence(GameMessage activityDescription) {
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
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            ActiveJob next = (ActiveJob)li.next();
            if (next.isValid(false)) continue;
            if (next.shouldClearSequence()) {
                return false;
            }
            li.remove();
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

