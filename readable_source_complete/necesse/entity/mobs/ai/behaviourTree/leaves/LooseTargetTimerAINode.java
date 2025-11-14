/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class LooseTargetTimerAINode<T extends Mob>
extends AINode<T> {
    public String currentTargetKey;
    public int looseTargetTimer;
    public int maxTargetLooseTicks = 120;

    public LooseTargetTimerAINode(String currentTargetKey) {
        this.currentTargetKey = currentTargetKey;
        this.startTimer();
    }

    public LooseTargetTimerAINode() {
        this("currentTarget");
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, this.currentTargetKey);
        if (target != null) {
            --this.looseTargetTimer;
            if (this.looseTargetTimer <= 0) {
                blackboard.put(this.currentTargetKey, null);
                this.startTimer();
                return AINodeResult.SUCCESS;
            }
        }
        return AINodeResult.FAILURE;
    }

    public void startTimer() {
        this.looseTargetTimer = GameRandom.globalRandom.getIntBetween(this.maxTargetLooseTicks / 2, this.maxTargetLooseTicks);
    }
}

