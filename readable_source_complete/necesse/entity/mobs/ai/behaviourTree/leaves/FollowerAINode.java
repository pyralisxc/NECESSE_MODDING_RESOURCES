/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.itemAttacker.FollowerPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class FollowerAINode<T extends Mob>
extends MoveTaskAINode<T> {
    private FollowerPosition targetPoint = null;
    private int attachedStage;
    private long nextPathFindTime;
    public int teleportDistance;
    public int stoppingDistance;
    public int directSearchDistance;
    public int teleportToAccuracy = 1;

    public FollowerAINode(int teleportDistance, int stoppingDistance) {
        this.teleportDistance = teleportDistance;
        this.stoppingDistance = stoppingDistance;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        if (this.directSearchDistance == 0) {
            int pathMoveDistance = (int)((Mob)mob).getPathMoveOffset().distance(0.0, 0.0);
            this.directSearchDistance = pathMoveDistance * 8 + 32;
        }
        blackboard.onWasHit(e -> {
            this.nextPathFindTime = 0L;
        });
        blackboard.onEvent("resetPathTime", e -> {
            this.nextPathFindTime = 0L;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        Mob target = this.getFollowingMob(mob);
        if (target != null && target.isSamePlace((Entity)mob)) {
            ItemAttackerMob followingAttacker = ((Mob)mob).getFollowingItemAttacker();
            this.targetPoint = followingAttacker == null ? null : followingAttacker.serverFollowersManager.getFollowerPos((Mob)mob, target, this.targetPoint);
            FollowerPosition finalPoint = this.targetPoint != null ? this.targetPoint : new FollowerPosition(0, 0);
            float dist = ((Mob)mob).getDistance(target.x, target.y);
            float posDist = (float)new Point(finalPoint.x, finalPoint.y).distance(0.0, 0.0);
            float targetDist = ((Mob)mob).getDistance(target.x + (float)finalPoint.x, target.y + (float)finalPoint.y);
            if (this.teleportDistance > 0 && dist > (float)this.teleportDistance && targetDist > (float)this.teleportDistance + posDist) {
                this.teleportTo(mob, target, this.teleportToAccuracy);
                this.attachedStage = 0;
            } else {
                MovedRectangle col = new MovedRectangle((Mob)mob, (int)(target.x + (float)finalPoint.x), (int)(target.y + (float)finalPoint.y));
                ComputedValue<Boolean> isTargetPathClear = new ComputedValue<Boolean>(() -> !mob.getLevel().collides((Shape)col, mob.getLevelCollisionFilter()));
                if (this.attachedStage == 0) {
                    if (isTargetPathClear.get().booleanValue()) {
                        this.attachedStage = 2;
                    } else if (this.nextPathFindTime <= ((Entity)mob).getWorldEntity().getLocalTime()) {
                        this.nextPathFindTime = ((Entity)mob).getWorldEntity().getLocalTime() + 1000L;
                        return this.moveToTileTask(target.getTileX(), target.getTileY(), null, path -> {
                            if (path.moveIfWithin(-1, -1, () -> {
                                this.nextPathFindTime = 0L;
                            })) {
                                int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 1000, 0.1f);
                                this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                            }
                            return AINodeResult.SUCCESS;
                        });
                    }
                } else if (this.attachedStage == 1) {
                    if (targetDist > posDist + (float)Math.max(this.stoppingDistance, this.directSearchDistance)) {
                        this.attachedStage = 0;
                    } else if ((targetDist < (float)this.stoppingDistance || targetDist < (float)this.directSearchDistance) && isTargetPathClear.get().booleanValue()) {
                        this.attachedStage = 2;
                    } else {
                        MobMovement mobMovement = finalPoint.movementGetter.apply(target);
                        blackboard.mover.setCustomMovement(this, mobMovement);
                    }
                } else {
                    blackboard.mover.setCustomMovement(this, finalPoint.movementGetter.apply(target));
                    if (this.targetPoint == null && targetDist < (float)this.stoppingDistance) {
                        blackboard.mover.stopMoving((Mob)mob);
                    }
                    if (!isTargetPathClear.get().booleanValue()) {
                        this.attachedStage = 0;
                    }
                }
            }
            return AINodeResult.SUCCESS;
        }
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            blackboard.mover.stopMoving((Mob)mob);
        }
        return AINodeResult.FAILURE;
    }

    public void teleportTo(T mob, Mob target, int maxTileRange) {
        FollowerAINode.teleportCloseTo(mob, target, maxTileRange);
        this.getBlackboard().mover.stopMoving((Mob)mob);
        ((Mob)mob).sendMovementPacket(true);
        this.nextPathFindTime = 0L;
    }

    public abstract Mob getFollowingMob(T var1);

    public static void teleportCloseTo(Mob mob, Mob target, int maxTileRange) {
        Point finalPos = FollowerAINode.getTeleportCloseToPos(mob, target, maxTileRange);
        mob.setPos(finalPos.x, finalPos.y, true);
    }

    public static Point getTeleportCloseToPos(Mob mob, Mob target, int maxTileRange) {
        int tileX = target.getTileX();
        int tileY = target.getTileY();
        Point pathOffset = mob.getPathMoveOffset();
        ArrayList<Point> possibleSpawns = new ArrayList<Point>();
        for (int x = tileX - maxTileRange; x <= tileX + maxTileRange; ++x) {
            for (int y = tileY - maxTileRange; y <= tileY + maxTileRange; ++y) {
                if (x == tileX && y == tileY) continue;
                int posX = x * 32 + pathOffset.x;
                int posY = y * 32 + pathOffset.y;
                if (mob.collidesWith(mob.getLevel(), posX, posY)) continue;
                possibleSpawns.add(new Point(posX, posY));
            }
        }
        Point finalPoint = (Point)GameRandom.globalRandom.getOneOf(possibleSpawns);
        if (finalPoint == null) {
            finalPoint = new Point(target.getX(), target.getY());
        }
        return finalPoint;
    }

    @Override
    public void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        tooltips.add("attachedStage: " + this.attachedStage);
        tooltips.add("nextPathFindTime: " + (this.nextPathFindTime - ((Entity)this.mob()).getWorldEntity().getLocalTime()));
    }
}

