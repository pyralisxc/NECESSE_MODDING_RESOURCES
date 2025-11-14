/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public abstract class CirclingFollowerAINode<T extends Mob>
extends AINode<T> {
    public int teleportDistance;
    public int teleportToAccuracy;
    public int circlingRange;
    private Point currentTargetOffset = new Point(0, 0);
    private int lastAngle;

    public CirclingFollowerAINode(int teleportDistance, int circlingRange) {
        this.teleportDistance = teleportDistance;
        this.circlingRange = circlingRange;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob target = this.getFollowingMob(mob);
        if (target != null && target.isSamePlace((Entity)mob)) {
            Point targetPoint = new Point(target.getX() + this.currentTargetOffset.x, target.getY() + this.currentTargetOffset.y);
            float dist = ((Mob)mob).getDistance(target.x, target.y);
            float posDist = (float)new Point(targetPoint.x, targetPoint.y).distance(0.0, 0.0);
            float targetDist = ((Mob)mob).getDistance(target.x + (float)targetPoint.x, target.y + (float)targetPoint.y);
            if (this.teleportDistance > 0 && dist > (float)this.teleportDistance && targetDist > (float)this.teleportDistance + posDist) {
                this.teleportTo(mob, target, this.teleportToAccuracy);
            } else {
                if (!blackboard.mover.isMoving() || ((Mob)mob).hasArrivedAtTarget()) {
                    this.findNewPosition(mob);
                }
                blackboard.mover.setCustomMovement(this, new MobMovementRelative(target, this.currentTargetOffset.x, this.currentTargetOffset.y, false, false));
            }
            return AINodeResult.SUCCESS;
        }
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            blackboard.mover.stopMoving((Mob)mob);
        }
        return AINodeResult.FAILURE;
    }

    public void findNewPosition(T mob) {
        int angle = this.lastAngle += GameRandom.globalRandom.getIntBetween(160, 240);
        float nx = (float)Math.cos(Math.toRadians(angle));
        float ny = (float)Math.sin(Math.toRadians(angle));
        this.currentTargetOffset = new Point((int)(nx * (float)this.circlingRange), (int)(ny * (float)this.circlingRange));
    }

    public void teleportTo(T mob, Mob target, int maxTileRange) {
        FollowerAINode.teleportCloseTo(mob, target, maxTileRange);
        this.getBlackboard().mover.stopMoving((Mob)mob);
        ((Mob)mob).sendMovementPacket(true);
    }

    public abstract Mob getFollowingMob(T var1);
}

