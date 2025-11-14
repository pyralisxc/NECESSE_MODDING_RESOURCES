/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.util.MoveToTileAITask;
import necesse.entity.mobs.ai.path.FinalPath;
import necesse.entity.mobs.ai.path.FinalPathPoint;
import necesse.entity.mobs.ai.path.PathOptions;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class AIMover {
    protected AINode<?> callerNode;
    protected FinalPath path;
    protected Runnable pathInvalidated;
    protected float targetX;
    protected float targetY;
    protected Mob targetMob;
    protected boolean targetStopWhenColliding;
    protected MobMovement custom;
    public int defaultMaxPathIterations = 1000;
    protected float lastX;
    protected float lastY;
    protected int stuck;

    public AIMover() {
    }

    public AIMover(int defaultMaxPathIterations) {
        this.defaultMaxPathIterations = defaultMaxPathIterations;
    }

    public boolean tick(Mob mob) {
        if (this.callerNode == null) {
            return false;
        }
        if (this.custom != null) {
            mob.setMovement(this.custom);
            return true;
        }
        if (this.targetMob != null) {
            mob.setMovement(new MobMovementRelative(this.targetMob, this.targetStopWhenColliding));
            return true;
        }
        return this.tickPath(mob);
    }

    public void resetStuck() {
        this.stuck = 0;
    }

    private boolean tickPath(Mob mob) {
        this.updatePath(mob);
        if (this.targetX != 0.0f && this.targetY != 0.0f) {
            mob.setMovement(new MobMovementLevelPos(this.targetX, this.targetY));
            if (Math.abs(this.lastX - mob.x) < 0.1f && Math.abs(this.lastY - mob.y) < 0.1f) {
                ++this.stuck;
                if (this.stuck > 40) {
                    this.stopMoving(mob);
                    this.callerNode = null;
                }
            }
            this.lastX = mob.x;
            this.lastY = mob.y;
            if (mob.hasArrivedAtTarget()) {
                this.stuck = 0;
                this.targetX = 0.0f;
                this.targetY = 0.0f;
                if (this.path != null) {
                    if (this.path.size() != 0) {
                        this.path.removeFirst();
                    } else {
                        this.path = null;
                    }
                    return this.tickPath(mob);
                }
            }
            return true;
        }
        this.stuck = 0;
        mob.stopMoving();
        return false;
    }

    private void updatePath(Mob mob) {
        if (this.path != null) {
            if (this.path.size() == 0) {
                this.path = null;
            } else {
                Point moveOffset = mob.getPathMoveOffset();
                FinalPathPoint p = this.path.getFirst();
                if (this.pathInvalidated != null && !p.checkValid.get().booleanValue()) {
                    this.pathInvalidated.run();
                    this.path = null;
                    this.targetX = 0.0f;
                    this.targetY = 0.0f;
                } else {
                    this.targetX = p.x * 32 + moveOffset.x;
                    this.targetY = p.y * 32 + moveOffset.y;
                }
            }
        }
    }

    public boolean isCurrentlyMovingFor(AINode<?> node) {
        return this.isMoving() && this.callerNode == node;
    }

    public AINode<?> getMovingFor() {
        return this.callerNode;
    }

    public boolean hasMovingNode() {
        return this.callerNode != null;
    }

    public void setMovingFor(AINode<?> node) {
        this.callerNode = node;
    }

    public boolean isMoving() {
        return this.custom != null || this.targetMob != null || this.targetX != 0.0f && this.targetY != 0.0f || this.path != null;
    }

    public boolean hasMobTarget() {
        return this.targetMob != null;
    }

    public Mob getTargetMob() {
        return this.targetMob;
    }

    protected void resetMoving() {
        this.custom = null;
        this.path = null;
        this.targetMob = null;
        this.targetX = 0.0f;
        this.targetY = 0.0f;
    }

    public void stopMoving(Mob mob) {
        this.resetMoving();
        this.callerNode = null;
        mob.stopMoving();
    }

    public void setCustomMovement(AINode<?> node, MobMovement movement) {
        this.resetMoving();
        this.callerNode = node;
        this.custom = movement;
    }

    public void setMobTarget(AINode<?> node, Mob target, boolean stopWhenColliding) {
        this.resetMoving();
        this.callerNode = node;
        this.targetMob = target;
        this.targetStopWhenColliding = stopWhenColliding;
    }

    public void setMobTarget(AINode<?> node, Mob target) {
        this.setMobTarget(node, target, false);
    }

    public void directMoveTo(AINode<?> node, int targetX, int targetY) {
        this.resetMoving();
        this.callerNode = node;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setPath(AINode<?> node, FinalPath path, Runnable pathInvalidated) {
        this.resetMoving();
        this.callerNode = node;
        if (this.path != path) {
            this.path = path;
            if (this.path.size() > 1) {
                this.path.removeFirst();
            }
        }
        this.pathInvalidated = pathInvalidated;
        this.updatePath((Mob)node.mob());
    }

    public MoveToTileAITask moveToTileTaskBare(AINode<?> node, int tileX, int tileY, BiPredicate<Point, Point> isAtTarget, int maxPathIterations, Function<MoveToTileAITask.AIPathResult, AINodeResult> handler) {
        return MoveToTileAITask.pathToTile(this, node, tileX, tileY, isAtTarget, maxPathIterations, this.getPathOptions(node), handler);
    }

    public MoveToTileAITask moveToTileTask(AINode<?> node, int tileX, int tileY, BiPredicate<Point, Point> isAtTarget, int maxPathIterations, Function<MoveToTileAITask.AIPathResult, AINodeResult> handler) {
        MoveToTileAITask task = this.moveToTileTaskBare(node, tileX, tileY, isAtTarget, maxPathIterations, handler);
        task.runConcurrently();
        return task;
    }

    public final MoveToTileAITask moveToTileTask(AINode<?> node, int tileX, int tileY, BiPredicate<Point, Point> isAtTarget, Function<MoveToTileAITask.AIPathResult, AINodeResult> handler) {
        return this.moveToTileTask(node, tileX, tileY, isAtTarget, this.defaultMaxPathIterations, handler);
    }

    public PathOptions getPathOptions(AINode<?> node) {
        Blackboard<?> blackboard = node.getBlackboard();
        PathOptions pathOptions = new PathOptions();
        if (blackboard != null) {
            pathOptions = blackboard.getObject(PathOptions.class, "pathOptions", pathOptions);
        }
        return pathOptions;
    }

    public boolean hasPath() {
        return this.path != null;
    }

    public Point getCurrentDestination() {
        if (this.path != null) {
            return this.path.getFirst();
        }
        return null;
    }

    public boolean isCurrentDestination(int tileX, int tileY) {
        Point current = this.getCurrentDestination();
        return current != null && current.x == tileX && current.y == tileY;
    }

    public boolean hasDestinationInPath(int tileX, int tileY) {
        if (this.targetMob != null && this.targetMob.getTileX() == tileX && this.targetMob.getTileY() == tileY) {
            return true;
        }
        if (this.path == null) {
            return false;
        }
        return this.path.streamPathPoints().anyMatch(p -> p.x == tileX && p.y == tileY);
    }

    public Point getFinalDestination() {
        if (this.path != null) {
            return this.path.getLast();
        }
        return null;
    }

    public boolean isFinalDestination(int tileX, int tileY) {
        Point last = this.getFinalDestination();
        return last != null && last.x == tileX && last.y == tileY;
    }

    public FinalPath getPath() {
        return this.path;
    }
}

