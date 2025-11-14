/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;

public class IsolateRunningAINode<T extends Mob>
extends DecoratorAINode<T> {
    public AINodeResult runningResult;

    public IsolateRunningAINode(AINode<T> child, AINodeResult runningResult) {
        super(child);
        this.runningResult = runningResult;
    }

    public IsolateRunningAINode(AINode<T> child) {
        this(child, AINodeResult.SUCCESS);
    }

    @Override
    public AINodeResult tickChild(AINode<T> child, T mob, Blackboard<T> blackboard) {
        child.lastResult = child.tick(mob, blackboard);
        AINodeResult result = child.lastResult;
        if (result == AINodeResult.RUNNING) {
            return this.runningResult;
        }
        return result;
    }
}

