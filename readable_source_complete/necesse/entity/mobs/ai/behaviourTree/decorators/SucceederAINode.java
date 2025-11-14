/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;

public class SucceederAINode<T extends Mob>
extends DecoratorAINode<T> {
    public SucceederAINode(AINode<T> child) {
        super(child);
    }

    @Override
    public AINodeResult tickChild(AINode<T> child, T mob, Blackboard<T> blackboard) {
        child.lastResult = child.tick(mob, blackboard);
        AINodeResult result = child.lastResult;
        if (result == AINodeResult.RUNNING) {
            return AINodeResult.RUNNING;
        }
        return AINodeResult.SUCCESS;
    }
}

