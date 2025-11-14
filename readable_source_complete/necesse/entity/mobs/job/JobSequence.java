/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public interface JobSequence {
    public GameMessage getActivityDescription();

    public void setActivityDescription(GameMessage var1);

    public void tick();

    public boolean hasNext();

    public ActiveJob next();

    public boolean isValid();

    public ActiveJobTargetFoundResult onTargetFound(Mob var1);

    public void cancel(boolean var1);
}

