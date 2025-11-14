/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.util.FutureAITask;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class TaskAINode<T extends Mob>
extends AINode<T> {
    private int tickCounter;
    private FutureAITask<?> task;

    public final AINodeResult startTask(FutureAITask<?> task) {
        this.tickCounter = 0;
        this.task = task;
        if (!task.isStarted()) {
            task.runConcurrently();
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.task != null) {
            ++this.tickCounter;
            if (this.task.isComplete()) {
                FutureAITask<?> temp = this.task;
                this.task = null;
                try {
                    AINodeResult result = temp.runComplete();
                    if (result != null) {
                        return result;
                    }
                    return this.tickNode(mob, blackboard);
                }
                catch (Exception e) {
                    throw new RuntimeException("Path error from " + ((Mob)mob).getStringID() + " (" + ((Entity)mob).getUniqueID() + ")", e);
                }
            }
            return this.tickWorking(mob, blackboard);
        }
        return this.tickNode(mob, blackboard);
    }

    public boolean hasTask() {
        return this.task != null;
    }

    public void clearTask() {
        this.task = null;
    }

    public abstract AINodeResult tickNode(T var1, Blackboard<T> var2);

    public AINodeResult tickWorking(T mob, Blackboard<T> blackboard) {
        return AINodeResult.RUNNING;
    }

    @Override
    public void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        if (this.task != null) {
            tooltips.add("Task: " + this.task + " running for " + this.tickCounter + " ticks");
        } else {
            tooltips.add("No current task");
        }
    }
}

