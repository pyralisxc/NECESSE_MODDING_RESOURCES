/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.MovedRectangle;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.util.MoveToTileAITask;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.level.maps.CollisionFilter;

public abstract class ChaserAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public BiPredicate<Mob, MoveToTileAITask.AIPathResult> moveIfFailedPath;
    public String targetKey;
    public long nextPathFindTime;
    public Mob lastTarget;
    public int attackDistance;
    public int stoppingDistance;
    public int lostTargetStopAttackingDistance = -1;
    public int minimumAttackDistance;
    public int runAwayDistance = -1;
    public int directSearchDistance;
    public int timeBeforeFirstAttack = 300;
    public boolean preferLand = true;
    public boolean smartPositioning;
    public boolean changePositionOnHit;
    public boolean forceMovePosition;
    public int moveSearchPositionRange = -1;
    public boolean changePositionConstantly;
    public int maxAttacksPerPosition = -1;
    public String chaserTargetKey = "chaserTarget";
    protected boolean movingToTarget = false;
    protected int attacksSincePositionChange;
    protected int canAttackTimer;
    protected boolean hasStartedAttack;

    public ChaserAINode(String targetKey, int attackDistance, boolean smartPositioning, boolean changePositionOnHit) {
        this.targetKey = targetKey;
        this.attackDistance = attackDistance;
        this.stoppingDistance = attackDistance - 20;
        this.smartPositioning = smartPositioning;
        this.changePositionOnHit = changePositionOnHit;
    }

    public ChaserAINode(int attackDistance, boolean smartPositioning, boolean changePositionOnHit) {
        this("currentTarget", attackDistance, smartPositioning, changePositionOnHit);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        if (this.directSearchDistance == 0) {
            int pathMoveDistance = (int)((Mob)mob).getPathMoveOffset().distance(0.0, 0.0);
            this.directSearchDistance = pathMoveDistance * 8 + 32;
        }
        blackboard.onEvent("resetPathTime", e -> {
            this.nextPathFindTime = 0L;
        });
        blackboard.onEvent("resetTarget", e -> this.getBlackboard().put(this.chaserTargetKey, null));
        blackboard.onEvent("chaserMovePosition", e -> {
            this.forceMovePosition = true;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        blackboard.put(this.chaserTargetKey, null);
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, this.targetKey);
        if (this.lastTarget != target) {
            this.nextPathFindTime = 0L;
        }
        this.lastTarget = target;
        if (target != null) {
            Iterator<AIWasHitEvent> iterator;
            boolean forceMoveAround = this.forceMovePosition;
            this.forceMovePosition = false;
            if (!forceMoveAround && this.smartPositioning && this.changePositionOnHit && (iterator = blackboard.getLastHits().iterator()).hasNext()) {
                AIWasHitEvent lastHit = iterator.next();
                forceMoveAround = true;
            }
            float distance = ((Mob)mob).getDistance(target);
            boolean canHitTarget = false;
            if (this.isTargetWithinAttackRange(mob, target, distance, this.hasStartedAttack) && this.canHitTarget(mob, ((Mob)mob).x, ((Mob)mob).y, target)) {
                this.hasStartedAttack = true;
                canHitTarget = true;
                if (this.canAttackTimer >= this.timeBeforeFirstAttack && this.attackTarget(mob, target)) {
                    ++this.attacksSincePositionChange;
                    if (this.maxAttacksPerPosition > 0 && this.attacksSincePositionChange >= this.maxAttacksPerPosition) {
                        forceMoveAround = true;
                    }
                }
                this.canAttackTimer += 50;
            } else {
                this.hasStartedAttack = false;
                this.canAttackTimer -= 50;
                if (this.canAttackTimer < 0) {
                    this.canAttackTimer = 0;
                }
            }
            if (this.smartPositioning && this.changePositionConstantly && !blackboard.mover.isCurrentlyMovingFor(this)) {
                forceMoveAround = true;
            }
            if (forceMoveAround) {
                this.movingToTarget = true;
                this.attacksSincePositionChange = 0;
                this.moveToTarget(mob, target, blackboard, null, () -> {
                    this.movingToTarget = false;
                    this.nextPathFindTime = 0L;
                });
            } else {
                boolean directChasing = false;
                if (!this.smartPositioning) {
                    ChaseDirection directChaseDirection = this.getDirectChaseDirection(mob, target, distance, canHitTarget);
                    switch (directChaseDirection) {
                        case INVALID: {
                            break;
                        }
                        case STAY: {
                            directChasing = true;
                            blackboard.mover.stopMoving((Mob)mob);
                            break;
                        }
                        case TOWARDS: {
                            directChasing = true;
                            blackboard.mover.setMobTarget(this, target);
                            break;
                        }
                        case AWAY: {
                            directChasing = true;
                            Point2D.Float targetDir = GameMath.normalize(((Mob)mob).x - target.x, ((Mob)mob).y - target.y);
                            float angle = GameMath.getAngle(targetDir);
                            int totalDirections = 8;
                            int totalDirectionsAngle = 360 / totalDirections;
                            angle = GameMath.fixAngle(angle + (float)totalDirectionsAngle / 2.0f);
                            int dir = (int)(angle / (float)totalDirectionsAngle);
                            Point2D.Float finalDir = new Point2D.Float();
                            int keepDistance = Math.max(this.minimumAttackDistance, this.runAwayDistance);
                            for (int i = 0; i < totalDirections / 2; ++i) {
                                int currentDir = dir - i;
                                finalDir = GameMath.getAngleDir(currentDir * totalDirectionsAngle);
                                int targetXPos = (int)(((Mob)mob).x + finalDir.x * (float)keepDistance);
                                int targetYPos = (int)(((Mob)mob).y + finalDir.y * (float)keepDistance);
                                if (!((Entity)mob).getLevel().collides((Shape)new MovedRectangle((Mob)mob, targetXPos, targetYPos), ((Mob)mob).getLevelCollisionFilter())) break;
                                if (i == 0) continue;
                                currentDir = dir + i;
                                finalDir = GameMath.getAngleDir(currentDir * totalDirectionsAngle);
                                targetXPos = (int)(((Mob)mob).x + finalDir.x * (float)keepDistance);
                                targetYPos = (int)(((Mob)mob).y + finalDir.y * (float)keepDistance);
                                if (!((Entity)mob).getLevel().collides((Shape)new MovedRectangle((Mob)mob, targetXPos, targetYPos), ((Mob)mob).getLevelCollisionFilter())) break;
                            }
                            int walkAwayDistance = keepDistance + ((Mob)mob).moveAccuracy;
                            blackboard.mover.setCustomMovement(this, new MobMovementRelative(target, finalDir.x * (float)walkAwayDistance, finalDir.y * (float)walkAwayDistance));
                        }
                    }
                    if (directChaseDirection != ChaseDirection.INVALID) {
                        this.nextPathFindTime = 0L;
                        this.movingToTarget = true;
                        this.attacksSincePositionChange = 0;
                    }
                }
                if (!directChasing && (!canHitTarget || this.stoppingDistance > 0 && distance > (float)this.stoppingDistance) && this.nextPathFindTime <= ((Entity)mob).getWorldEntity().getLocalTime()) {
                    this.moveToTarget(mob, target, blackboard, (result, path) -> {
                        if (result == PathResult.SUCCESS || result == PathResult.MOVED_NO_SUCCESS) {
                            this.movingToTarget = true;
                            this.attacksSincePositionChange = 0;
                            if (result == PathResult.MOVED_NO_SUCCESS) {
                                int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 5000, 0.1f);
                                this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                            } else {
                                int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 500, 0.1f);
                                this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                            }
                        } else {
                            this.movingToTarget = false;
                            this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)(1000 * (GameRandom.globalRandom.nextInt(5) + 3));
                        }
                    }, () -> {
                        this.movingToTarget = false;
                        this.nextPathFindTime = 0L;
                    });
                }
            }
        } else {
            this.movingToTarget = false;
        }
        return this.lastTick(this.movingToTarget, target);
    }

    protected AINodeResult lastTick(boolean movingToTarget, Mob target) {
        if (movingToTarget) {
            this.getBlackboard().put(this.chaserTargetKey, target);
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }

    public boolean isTargetWithinAttackRange(T mob, Mob target, float distanceToTarget, boolean hasStartedAttack) {
        int lostDistance = hasStartedAttack ? Math.max(this.attackDistance, this.lostTargetStopAttackingDistance) : this.attackDistance;
        return distanceToTarget <= (float)lostDistance && distanceToTarget >= (float)this.minimumAttackDistance;
    }

    public ChaseDirection getDirectChaseDirection(T mob, Mob target, float distanceToTarget, boolean canHitTarget) {
        if (distanceToTarget >= (float)this.stoppingDistance && distanceToTarget >= (float)this.directSearchDistance) {
            return ChaseDirection.INVALID;
        }
        CollisionFilter collisionFilter = ((Mob)mob).modifyChasingCollisionFilter(((Mob)mob).getLevelCollisionFilter(), target);
        if (((Entity)mob).getLevel().collides((Shape)new MovedRectangle((Mob)mob, target.getX(), target.getY()), collisionFilter)) {
            return ChaseDirection.INVALID;
        }
        if (distanceToTarget < (float)this.minimumAttackDistance || distanceToTarget < (float)this.runAwayDistance) {
            return ChaseDirection.AWAY;
        }
        if (canHitTarget && distanceToTarget < (float)this.stoppingDistance) {
            return ChaseDirection.STAY;
        }
        return ChaseDirection.TOWARDS;
    }

    public boolean canHitTarget(T mob, float fromX, float fromY, Mob target) {
        return ChaserAINode.hasLineOfSightToTarget(mob, fromX, fromY, target);
    }

    public static boolean hasLineOfSightToTarget(Mob mob, float fromX, float fromY, float startOffset, Mob target, float hitBoxWidth) {
        CollisionFilter collisionFilter = mob.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target);
        if (startOffset != 0.0f) {
            Point2D.Float dir = GameMath.normalize(target.x - fromX, target.y - fromY);
            fromX += dir.x * startOffset;
            fromY += dir.y * startOffset;
        }
        if (hitBoxWidth > 0.0f) {
            return !mob.getLevel().collides((Shape)new LineHitbox(fromX, fromY, target.x, target.y, hitBoxWidth), collisionFilter);
        }
        return !mob.getLevel().collides(new Line2D.Float(fromX, fromY, target.x, target.y), collisionFilter);
    }

    public static boolean hasLineOfSightToTarget(Mob mob, float fromX, float fromY, Mob target, float hitBoxWidth) {
        return ChaserAINode.hasLineOfSightToTarget(mob, fromX, fromY, 0.0f, target, hitBoxWidth);
    }

    public static boolean hasLineOfSightToTarget(Mob mob, float fromX, float fromY, Mob target) {
        return ChaserAINode.hasLineOfSightToTarget(mob, fromX, fromY, target, 0.0f);
    }

    public static boolean hasLineOfSightToTarget(Mob from, Mob target, float hitBoxWidth) {
        return ChaserAINode.hasLineOfSightToTarget(from, from.x, from.y, target, hitBoxWidth);
    }

    public static boolean hasLineOfSightToTarget(Mob from, Mob target) {
        return ChaserAINode.hasLineOfSightToTarget(from, target, 0.0f);
    }

    public static boolean isTargetHitboxWithinRange(Mob mob, float fromX, float fromY, Mob target, float range) {
        Point2D.Float dir = GameMath.normalize(target.x - fromX, target.y - fromY);
        return target.getHitBox().intersectsLine(fromX, fromY, fromX + dir.x * range, fromY + dir.y * range);
    }

    public abstract boolean attackTarget(T var1, Mob var2);

    public void moveToTarget(T mob, Mob target, Blackboard<T> blackboard, BiConsumer<PathResult, MoveToTileAITask.AIPathResult> moving, Runnable pathInvalidated) {
        int searchLevelRange;
        int searchTileRange;
        if (target == null) {
            if (moving != null) {
                moving.accept(PathResult.FAILED, null);
            }
            return;
        }
        if (!this.smartPositioning) {
            this.moveToTileTask(target.getTileX(), target.getTileY(), null, path -> {
                boolean result = path.moveIfWithin(-1, this.moveIfFailedPath != null && this.moveIfFailedPath.test(target, (MoveToTileAITask.AIPathResult)path) ? -1 : 1, pathInvalidated);
                if (moving != null) {
                    if (result) {
                        if (path.isResultWithin(1)) {
                            moving.accept(PathResult.SUCCESS, (MoveToTileAITask.AIPathResult)path);
                        } else {
                            moving.accept(PathResult.MOVED_NO_SUCCESS, (MoveToTileAITask.AIPathResult)path);
                        }
                    } else {
                        moving.accept(PathResult.FAILED, (MoveToTileAITask.AIPathResult)path);
                    }
                }
                return this.lastTick(this.movingToTarget, target);
            });
            return;
        }
        ArrayList<Point> firstPositions = new ArrayList<Point>();
        ArrayList<Point> secondPositions = new ArrayList<Point>();
        if (this.moveSearchPositionRange >= 0) {
            searchTileRange = this.moveSearchPositionRange / 32;
            searchLevelRange = this.moveSearchPositionRange;
        } else {
            searchTileRange = (this.attackDistance - 1) / 32;
            searchLevelRange = this.attackDistance;
        }
        for (int x = -searchTileRange; x <= searchTileRange; ++x) {
            int actualX = target.getTileX() + x;
            for (int y = -searchTileRange; y <= searchTileRange; ++y) {
                float distance;
                int actualY = target.getTileY() + y;
                if (((Entity)mob).getLevel().isSolidTile(actualX, actualY) || (distance = target.getDistance(actualX * 32 + 16, actualY * 32 + 16)) < 64.0f || distance > (float)searchLevelRange || !this.canHitTarget(mob, actualX * 32 + 16, actualY * 32 + 16, target)) continue;
                if (this.preferLand && ((Entity)mob).getLevel().isLiquidTile(actualX, actualY)) {
                    secondPositions.add(new Point(actualX, actualY));
                    continue;
                }
                firstPositions.add(new Point(actualX, actualY));
            }
        }
        Point finalPoint = null;
        if (firstPositions.size() != 0) {
            finalPoint = (Point)firstPositions.get(GameRandom.globalRandom.nextInt(firstPositions.size()));
        } else if (secondPositions.size() != 0) {
            finalPoint = (Point)secondPositions.get(GameRandom.globalRandom.nextInt(secondPositions.size()));
        }
        if (finalPoint != null) {
            this.moveToTileTask(finalPoint.x, finalPoint.y, null, path -> {
                boolean result = path.moveIfWithin(-1, this.moveIfFailedPath != null && this.moveIfFailedPath.test(target, (MoveToTileAITask.AIPathResult)path) ? -1 : 1, pathInvalidated);
                if (moving != null) {
                    if (result) {
                        if (path.isResultWithin(1)) {
                            moving.accept(PathResult.SUCCESS, (MoveToTileAITask.AIPathResult)path);
                        } else {
                            moving.accept(PathResult.MOVED_NO_SUCCESS, (MoveToTileAITask.AIPathResult)path);
                        }
                    } else {
                        moving.accept(PathResult.FAILED, (MoveToTileAITask.AIPathResult)path);
                    }
                }
                return this.lastTick(this.movingToTarget, target);
            });
            return;
        }
        if (moving != null) {
            moving.accept(PathResult.FAILED, null);
        }
    }

    public static enum ChaseDirection {
        TOWARDS,
        STAY,
        AWAY,
        INVALID;

    }

    protected static enum PathResult {
        SUCCESS,
        MOVED_NO_SUCCESS,
        FAILED;

    }
}

