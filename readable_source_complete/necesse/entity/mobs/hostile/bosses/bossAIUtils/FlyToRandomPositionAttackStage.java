/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class FlyToRandomPositionAttackStage<T extends Mob>
extends AINode<T>
implements AttackStageInterface<T> {
    public int baseDistance;
    public boolean isRunningWhileMoving;
    public String targetKey;

    public FlyToRandomPositionAttackStage(boolean isRunningWhileMoving, int baseDistance, String targetKey) {
        this.isRunningWhileMoving = isRunningWhileMoving;
        this.baseDistance = baseDistance;
        this.targetKey = targetKey;
    }

    public FlyToRandomPositionAttackStage(boolean isRunningWhileMoving, int baseDistance) {
        this(isRunningWhileMoving, baseDistance, "currentTarget");
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Mob currentTarget = blackboard.getObject(Mob.class, this.targetKey);
        Point2D.Float base = new Point2D.Float(((Mob)mob).x, ((Mob)mob).y);
        if (currentTarget != null) {
            base = new Point2D.Float(currentTarget.x, currentTarget.y);
        }
        Point2D.Float pos = new Point2D.Float(((Mob)mob).x, ((Mob)mob).y);
        for (int i = 0; i < 10; ++i) {
            int randomAngle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float angleDir = GameMath.getAngleDir(randomAngle);
            pos = new Point2D.Float(base.x + angleDir.x * (float)this.baseDistance, base.y + angleDir.y * (float)this.baseDistance);
            if (((Mob)mob).getDistance(pos.x, pos.y) >= (float)this.baseDistance / 4.0f) break;
        }
        blackboard.mover.directMoveTo(this, (int)pos.x, (int)pos.y);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.isRunningWhileMoving && blackboard.mover.isMoving()) {
            return AINodeResult.RUNNING;
        }
        return AINodeResult.SUCCESS;
    }
}

