/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.Entity;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;

public class HumanJobSearchingAINode<T extends HumanMob>
extends AINode<T> {
    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (!mob.isBeingInteractedWith()) {
            AINodeResult result = Performance.record((PerformanceTimerManager)((Entity)mob).getLevel().tickManager(), "humanJobSearch", () -> {
                ActiveJob lastJob = blackboard.getObject(ActiveJob.class, "currentJob");
                if (lastJob != null) {
                    return AINodeResult.SUCCESS;
                }
                JobSequence lastSequence = blackboard.getObject(JobSequence.class, "currentJobSequence");
                if (lastSequence == null) {
                    mob.resetJobCancelled();
                    lastSequence = mob.findJob();
                } else if (mob.isJobCancelled()) {
                    lastSequence.cancel(false);
                    mob.resetJobCancelled();
                }
                if (lastSequence != null && lastSequence.hasNext()) {
                    ActiveJob next;
                    if (mob.objectUser != null) {
                        mob.objectUser.stopUsing();
                    }
                    if ((next = lastSequence.next()) != null) {
                        next.onMadeCurrent();
                    }
                    blackboard.put("currentJobSequence", lastSequence);
                    blackboard.put("currentJob", next);
                    GameMessage activityDescription = lastSequence.getActivityDescription();
                    if (activityDescription != null) {
                        mob.setActivity("job", 10000, activityDescription);
                    }
                    return AINodeResult.SUCCESS;
                }
                return null;
            });
            if (result != null) {
                return result;
            }
            blackboard.put("currentJobSequence", null);
            blackboard.put("currentJob", null);
            ((HumanMob)mob).resetJobCancelled();
        }
        ((HumanMob)mob).clearActivity(null);
        return AINodeResult.FAILURE;
    }
}

