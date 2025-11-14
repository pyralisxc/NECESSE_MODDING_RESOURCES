/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.TargetAIEvent;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobHitResult;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public class HumanDoJobAINode<T extends HumanMob>
extends AINode<T> {
    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        ActiveJob currentJob = blackboard.getObject(ActiveJob.class, "currentJob");
        if (currentJob != null) {
            return Performance.record((PerformanceTimerManager)((Entity)mob).getLevel().tickManager(), "humanJobPerform", () -> Performance.record((PerformanceTimerManager)mob.getLevel().tickManager(), currentJob.priority == null ? "null" : currentJob.priority.type.getStringID(), () -> {
                boolean isInvalid;
                JobSequence currentSequence = blackboard.getObject(JobSequence.class, "currentJobSequence");
                if (currentJob.priority != null && currentJob.priority.disabledByPlayer) {
                    currentJob.onCancelled(false, true, true);
                    if (currentSequence != null) {
                        currentSequence.cancel(false);
                    }
                    blackboard.put("currentJobSequence", null);
                    blackboard.put("currentJob", null);
                    return AINodeResult.FAILURE;
                }
                for (AIWasHitEvent e : blackboard.getLastHits()) {
                    if (e.event.wasPrevented) continue;
                    ActiveJobHitResult result = currentJob.onHit(e.event, false);
                    switch (result) {
                        case CLEAR_SEQUENCE: {
                            currentJob.onCancelled(false, true, true);
                            if (currentSequence != null) {
                                currentSequence.cancel(false);
                            }
                            blackboard.put("currentJobSequence", null);
                            blackboard.put("currentJob", null);
                            currentJob.worker.onHitCausedFailed(true);
                            return AINodeResult.FAILURE;
                        }
                        case CLEAR_THIS: {
                            currentJob.onCancelled(false, true, true);
                            blackboard.put("currentJob", null);
                            currentJob.worker.onHitCausedFailed(false);
                            return AINodeResult.SUCCESS;
                        }
                        case MOVE_TO: {
                            return AINodeResult.SUCCESS;
                        }
                    }
                }
                for (AIEvent found : blackboard.getLastCustomEvents("newTargetFound")) {
                    ActiveJobTargetFoundResult sequenceResult;
                    if (!(found instanceof TargetAIEvent)) continue;
                    Mob target = ((TargetAIEvent)found).target;
                    ActiveJobTargetFoundResult result = currentJob.onTargetFound(target, true, false);
                    if (currentSequence != null && (sequenceResult = currentSequence.onTargetFound(target)) == ActiveJobTargetFoundResult.FAIL) {
                        result = sequenceResult;
                    }
                    switch (result) {
                        case CONTINUE: {
                            break;
                        }
                        case FAIL: {
                            currentJob.onCancelled(false, true, true);
                            if (currentSequence != null) {
                                currentSequence.cancel(false);
                            }
                            blackboard.put("currentJobSequence", null);
                            blackboard.put("currentJob", null);
                            currentJob.worker.onTargetFoundCausedFailed(target);
                            return AINodeResult.FAILURE;
                        }
                    }
                }
                boolean bl = isInvalid = !currentJob.isValid(true);
                if (isInvalid || mob.isJobCancelled()) {
                    blackboard.mover.stopMoving((Mob)mob);
                    blackboard.put("currentJob", null);
                    if (currentJob.shouldClearSequence()) {
                        blackboard.put("currentJobSequence", null);
                    }
                    currentJob.onCancelled(isInvalid, true, true);
                    if (currentSequence != null) {
                        currentSequence.cancel(isInvalid);
                    }
                    mob.resetJobCancelled();
                    return AINodeResult.FAILURE;
                }
                currentJob.tick(true, false);
                if (currentSequence != null) {
                    currentSequence.tick();
                    GameMessage activityDescription = currentSequence.getActivityDescription();
                    if (activityDescription != null) {
                        mob.setActivity("job", 10000, activityDescription);
                    }
                }
                if (!mob.isBeingInteractedWith()) {
                    ActiveJobResult result = currentJob.perform();
                    switch (result) {
                        case PERFORMING: {
                            return AINodeResult.RUNNING;
                        }
                        case MOVE_TO: {
                            return AINodeResult.SUCCESS;
                        }
                        case FINISHED: {
                            blackboard.put("currentJob", null);
                            blackboard.submitEvent("wanderNow", new AIEvent());
                            return AINodeResult.SUCCESS;
                        }
                    }
                    blackboard.put("currentJobSequence", null);
                    blackboard.put("currentJob", null);
                    return AINodeResult.FAILURE;
                }
                blackboard.mover.stopMoving((Mob)mob);
                return AINodeResult.RUNNING;
            }));
        }
        return AINodeResult.FAILURE;
    }
}

