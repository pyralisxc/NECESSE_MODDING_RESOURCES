/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class RunningAINode<T extends Mob>
extends AINode<T> {
    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public final AINodeResult tick(T mob, Blackboard<T> blackboard) {
        AINodeResult result;
        if (this.lastResult != AINodeResult.RUNNING) {
            this.start(mob, blackboard);
        }
        if ((result = this.tickRunning(mob, blackboard)) != AINodeResult.RUNNING) {
            this.end(mob, blackboard);
        }
        return result;
    }

    public abstract void start(T var1, Blackboard<T> var2);

    public abstract AINodeResult tickRunning(T var1, Blackboard<T> var2);

    public abstract void end(T var1, Blackboard<T> var2);
}

