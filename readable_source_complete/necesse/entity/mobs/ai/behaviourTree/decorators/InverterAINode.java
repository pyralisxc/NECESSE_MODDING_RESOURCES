/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;

public class InverterAINode<T extends Mob>
extends DecoratorAINode<T> {
    public InverterAINode(AINode<T> child) {
        super(child);
    }

    @Override
    public AINodeResult tickChild(AINode<T> child, T mob, Blackboard<T> blackboard) {
        AINodeResult result = child.lastResult = child.tick(mob, blackboard);
        switch (result) {
            case RUNNING: {
                return AINodeResult.RUNNING;
            }
            case FAILURE: {
                return AINodeResult.SUCCESS;
            }
            case SUCCESS: {
                return AINodeResult.FAILURE;
            }
        }
        return null;
    }
}

