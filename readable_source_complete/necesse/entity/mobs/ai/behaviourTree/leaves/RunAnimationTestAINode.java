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
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class RunAnimationTestAINode<T extends Mob>
extends AINode<T> {
    private int radius;
    private int baseX;
    private int baseY;
    private int currentPos = 0;
    private Point[] positions = new Point[]{new Point(-1, -2), new Point(1, -2), new Point(2, -1), new Point(2, 1), new Point(1, 2), new Point(-1, 2), new Point(-2, 1), new Point(-2, -1)};

    public RunAnimationTestAINode(int radius) {
        this.radius = radius;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        this.baseX = ((Entity)mob).getX();
        this.baseY = ((Entity)mob).getY();
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (((Mob)mob).hasArrivedAtTarget()) {
            this.currentPos = (this.currentPos + 1) % this.positions.length;
            Point positionOffset = this.positions[this.currentPos];
            int posX = this.baseX + positionOffset.x * this.radius / 2;
            int posY = this.baseY + positionOffset.y * this.radius / 2;
            ((Mob)mob).setMovement(new MobMovementLevelPos(posX, posY));
        }
        return AINodeResult.SUCCESS;
    }
}

