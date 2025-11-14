/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree;

import java.util.Collections;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class DecoratorAINode<T extends Mob>
extends AINode<T> {
    private boolean rootSet;
    private AINode<T> child;

    public DecoratorAINode(AINode<T> child) {
        this.child = child;
    }

    public AINode<T> getChild() {
        return this.child;
    }

    protected void setChild(AINode<T> child) {
        this.child = child;
        if (this.rootSet && child != null) {
            child.setChildRoot(this);
        }
    }

    @Override
    public void onInterruptRunning(T mob, Blackboard<T> blackboard) {
        if (this.child != null) {
            this.child.onInterruptRunning(mob, blackboard);
        }
    }

    @Override
    protected void onForceSetRunning(AINode<T> node) {
        if (this.child != null) {
            this.child.onForceSetRunning(node);
        }
    }

    @Override
    public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        this.rootSet = true;
        if (this.child != null) {
            this.child.setChildRoot(this);
        }
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        if (this.child != null) {
            this.child.lastResult = null;
            this.child.init(mob, blackboard);
        }
    }

    @Override
    public Iterable<AINode<T>> debugChildren() {
        if (this.child == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(this.child);
    }

    @Override
    public final AINodeResult tick(T mob, Blackboard<T> blackboard) {
        return this.tickChild(this.child, mob, blackboard);
    }

    public abstract AINodeResult tickChild(AINode<T> var1, T var2, Blackboard<T> var3);
}

