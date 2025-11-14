/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.function.BiPredicate;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.TargetAIEvent;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobHitResult;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public class HumanJobMoveToAINode<T extends HumanMob>
extends MoveTaskAINode<T> {
    public int pathsSinceProgress;
    public double lastPathDistToTarget;
    public long nextMoveTime;
    public long nextValidCheck;
    public JobMoveToTile jobMoveTile;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onEvent("resetPathTime", e -> {
            this.nextMoveTime = 0L;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        this.nextMoveTime = 0L;
        this.nextValidCheck = 0L;
        this.jobMoveTile = null;
        this.pathsSinceProgress = 0;
        this.lastPathDistToTarget = -1.0;
    }

    protected boolean checkValid(T mob, Blackboard<T> blackboard) {
        long currentTime = ((Entity)mob).getWorldEntity().getTime();
        if (this.nextValidCheck <= currentTime) {
            ActiveJob currentJob = blackboard.getObject(ActiveJob.class, "currentJob");
            JobSequence currentSequence = blackboard.getObject(JobSequence.class, "currentJobSequence");
            if (!currentJob.isValid(false)) {
                blackboard.mover.stopMoving((Mob)mob);
                blackboard.put("currentJob", null);
                if (currentJob.shouldClearSequence()) {
                    blackboard.put("currentJobSequence", null);
                }
                return false;
            }
            if (currentSequence != null && !currentSequence.isValid()) {
                blackboard.mover.stopMoving((Mob)mob);
                blackboard.put("currentJob", null);
                blackboard.put("currentJobSequence", null);
                return false;
            }
            this.nextValidCheck = currentTime + 2000L;
        }
        return true;
    }

    @Override
    public AINodeResult tickWorking(T mob, Blackboard<T> blackboard) {
        ActiveJob currentJob = blackboard.getObject(ActiveJob.class, "currentJob");
        if (!(currentJob == null || currentJob.priority != null && currentJob.priority.disabledByPlayer)) {
            return Performance.record((PerformanceTimerManager)((Entity)mob).getLevel().tickManager(), "humanJobMoveTo", () -> {
                boolean isInvalid;
                JobSequence currentSequence = blackboard.getObject(JobSequence.class, "currentJobSequence");
                boolean bl = isInvalid = !this.checkValid(mob, blackboard);
                if (isInvalid || mob.isJobCancelled()) {
                    currentJob.onCancelled(isInvalid, true, true);
                    if (currentSequence != null) {
                        currentSequence.cancel(isInvalid);
                    }
                    mob.resetJobCancelled();
                    this.clearTask();
                    return AINodeResult.FAILURE;
                }
                currentJob.tick(true, true);
                if (currentSequence != null) {
                    currentSequence.tick();
                }
                return AINodeResult.RUNNING;
            });
        }
        return super.tickWorking(mob, blackboard);
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        ActiveJob currentJob = blackboard.getObject(ActiveJob.class, "currentJob");
        if (currentJob != null) {
            return Performance.record((PerformanceTimerManager)((Entity)mob).getLevel().tickManager(), "humanJobMoveTo", () -> {
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
                    ActiveJobHitResult hitResult = currentJob.onHit(e.event, false);
                    switch (hitResult) {
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
                    }
                }
                for (AIEvent found : blackboard.getLastCustomEvents("newTargetFound")) {
                    ActiveJobTargetFoundResult sequenceResult;
                    if (!(found instanceof TargetAIEvent)) continue;
                    Mob target = ((TargetAIEvent)found).target;
                    ActiveJobTargetFoundResult result = currentJob.onTargetFound(target, true, true);
                    ActiveJobTargetFoundResult activeJobTargetFoundResult = sequenceResult = currentSequence == null ? null : currentSequence.onTargetFound(target);
                    if (sequenceResult == ActiveJobTargetFoundResult.FAIL) {
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
                boolean bl = isInvalid = !this.checkValid(mob, blackboard);
                if (isInvalid || mob.isJobCancelled()) {
                    currentJob.onCancelled(isInvalid, true, true);
                    if (currentSequence != null) {
                        currentSequence.cancel(isInvalid);
                    }
                    mob.resetJobCancelled();
                    return AINodeResult.FAILURE;
                }
                currentJob.tick(true, true);
                if (currentSequence != null) {
                    currentSequence.tick();
                    GameMessage activityDescription = currentSequence.getActivityDescription();
                    if (activityDescription != null) {
                        mob.setActivity("job", 10000, activityDescription);
                    }
                }
                if (!mob.isBeingInteractedWith()) {
                    JobMoveToTile last = this.jobMoveTile;
                    this.jobMoveTile = currentJob.getMoveToTile(this.jobMoveTile);
                    if (this.jobMoveTile == null) {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.SUCCESS;
                    }
                    if (currentJob.isAt(this.jobMoveTile)) {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.SUCCESS;
                    }
                    if (this.jobMoveTile.custom != null) {
                        blackboard.mover.setCustomMovement(this, this.jobMoveTile.custom);
                        this.pathsSinceProgress = 0;
                        return AINodeResult.RUNNING;
                    }
                    if (!this.jobMoveTile.equals(last)) {
                        this.nextMoveTime = 0L;
                    }
                    long currentTime = mob.getWorldEntity().getLocalTime();
                    if (this.pathsSinceProgress > 5) {
                        blackboard.put("currentJob", null);
                        return AINodeResult.FAILURE;
                    }
                    if (this.nextMoveTime <= currentTime) {
                        this.nextMoveTime = currentTime + 5000L;
                        if (mob.estimateCanMoveTo(this.jobMoveTile.tileX, this.jobMoveTile.tileY, this.jobMoveTile.acceptAdjacentTiles)) {
                            BiPredicate<Point, Point> isAtTarget = this.jobMoveTile.acceptAdjacentTiles ? TilePathfinding.isAtOrAdjacentObject(mob.getLevel(), this.jobMoveTile.tileX, this.jobMoveTile.tileY) : null;
                            JobMoveToTile temp = this.jobMoveTile;
                            return this.moveToTileTask(temp.tileX, temp.tileY, isAtTarget, temp.maxPathIterations, path -> {
                                if (path.result.foundTarget || temp.acceptAdjacentTiles && path.isResultWithin(1)) {
                                    this.pathsSinceProgress = 0;
                                    this.lastPathDistToTarget = 0.0;
                                } else {
                                    Point lastNode = path.result.getLastPathResult();
                                    if (lastNode == null) {
                                        ++this.pathsSinceProgress;
                                    } else {
                                        double pathDistToTarget = lastNode.distance((Point2D)path.result.target);
                                        if (this.lastPathDistToTarget < 0.0 || pathDistToTarget == 0.0 || pathDistToTarget < this.lastPathDistToTarget) {
                                            this.pathsSinceProgress = 0;
                                            this.lastPathDistToTarget = pathDistToTarget;
                                        } else {
                                            ++this.pathsSinceProgress;
                                        }
                                    }
                                }
                                if (path.moveIfWithin(-1, -1, () -> {
                                    this.nextMoveTime = 0L;
                                })) {
                                    int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 5000, 0.1f);
                                    this.nextMoveTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                                }
                                return AINodeResult.RUNNING;
                            });
                        }
                        ++this.pathsSinceProgress;
                    }
                    return AINodeResult.RUNNING;
                }
                blackboard.mover.stopMoving((Mob)mob);
                return AINodeResult.RUNNING;
            });
        }
        return AINodeResult.FAILURE;
    }
}

