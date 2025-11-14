/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class RemoveOnNoTargetNode<T extends Mob>
extends AINode<T> {
    public String targetKey = "currentTarget";
    public int counter;
    public int removeAfterTicks;

    public RemoveOnNoTargetNode(int removeAfterTicks) {
        this.removeAfterTicks = removeAfterTicks;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onEvent("refreshBossDespawn", event -> {
            this.counter = 0;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob currentTarget = blackboard.getObject(Mob.class, this.targetKey);
        if (currentTarget == null) {
            ++this.counter;
            if (this.counter > this.removeAfterTicks) {
                ((Mob)mob).remove();
            }
        } else {
            this.counter = 0;
        }
        return AINodeResult.SUCCESS;
    }
}

