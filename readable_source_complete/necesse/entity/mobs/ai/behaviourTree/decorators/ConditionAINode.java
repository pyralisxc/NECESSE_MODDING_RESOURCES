/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.decorators;

import java.util.function.Predicate;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;

public class ConditionAINode<T extends Mob>
extends DecoratorAINode<T> {
    public Predicate<T> condition;
    public AINodeResult failResult;

    public ConditionAINode(AINode<T> child, Predicate<T> condition, AINodeResult failResult) {
        super(child);
        this.condition = condition;
        this.failResult = failResult;
    }

    @Override
    public AINodeResult tickChild(AINode<T> child, T mob, Blackboard<T> blackboard) {
        if (child.lastResult == AINodeResult.RUNNING || this.condition.test(mob)) {
            child.lastResult = child.tick(mob, blackboard);
            return child.lastResult;
        }
        return this.failResult;
    }
}

