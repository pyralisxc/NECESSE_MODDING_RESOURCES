/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.composites;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.CompositeAINode;

public class SelectorAINode<T extends Mob>
extends CompositeAINode<T> {
    @Override
    protected AINodeResult tickChildren(AINode<T> lastRunningChild, AINodeResult runningChildResult, Iterable<AINode<T>> children, T mob, Blackboard<T> blackboard) {
        if (runningChildResult == AINodeResult.SUCCESS) {
            return AINodeResult.SUCCESS;
        }
        for (AINode<T> child : children) {
            AINodeResult result = child.lastResult = child.tick(mob, blackboard);
            switch (result) {
                case RUNNING: {
                    this.runningNode = child;
                    return AINodeResult.RUNNING;
                }
                case SUCCESS: {
                    return AINodeResult.SUCCESS;
                }
            }
        }
        return AINodeResult.FAILURE;
    }
}

