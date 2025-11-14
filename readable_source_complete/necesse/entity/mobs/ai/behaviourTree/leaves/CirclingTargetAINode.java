/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class CirclingTargetAINode<T extends Mob>
extends AINode<T> {
    public String targetKey = "currentTarget";
    public String chaserTargetKey = "chaserTarget";
    public int circlingRange;
    public int nextAngleOffset;
    private Point currentTargetOffset = new Point(0, 0);
    private int lastAngle;

    public CirclingTargetAINode(int circlingRange, int nextAngleOffset) {
        this.circlingRange = circlingRange;
        this.nextAngleOffset = nextAngleOffset;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        blackboard.put(this.chaserTargetKey, null);
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, this.targetKey);
        if (target != null && target.isSamePlace((Entity)mob)) {
            if (!blackboard.mover.isMoving() || ((Mob)mob).hasArrivedAtTarget()) {
                this.findNewPosition(mob);
            }
            blackboard.mover.setCustomMovement(this, new MobMovementRelative(target, this.currentTargetOffset.x, this.currentTargetOffset.y, false, false));
            blackboard.put(this.chaserTargetKey, target);
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }

    public void findNewPosition(T mob) {
        int angle = this.lastAngle += this.nextAngleOffset;
        float nx = (float)Math.cos(Math.toRadians(angle));
        float ny = (float)Math.sin(Math.toRadians(angle));
        this.currentTargetOffset = new Point((int)(nx * (float)this.circlingRange), (int)(ny * (float)this.circlingRange));
    }
}

