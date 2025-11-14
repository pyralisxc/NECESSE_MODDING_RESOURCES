/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class StopMovingAttackStage<T extends Mob>
extends AINode<T>
implements AttackStageInterface<T> {
    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        blackboard.mover.stopMoving((Mob)mob);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        return AINodeResult.SUCCESS;
    }
}

