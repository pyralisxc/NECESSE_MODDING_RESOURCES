/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree;

import java.util.Collections;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class AINode<T extends Mob> {
    public AINodeResult lastResult;
    private AINode<T> root;
    private AINode<T> parent;
    private T mob;
    private Blackboard<T> blackboard;

    public final void makeRoot(T mob, Blackboard<T> blackboard) {
        if (this.root != null) {
            throw new IllegalStateException("Cannot make node root if already has root");
        }
        this.root = this;
        this.parent = null;
        this.mob = mob;
        this.blackboard = blackboard;
        this.onRootSet(this.root, mob, blackboard);
    }

    protected final void setChildRoot(AINode<T> parent) {
        if (this.root == this) {
            throw new IllegalStateException("Cannot set child root on root node");
        }
        this.parent = parent;
        if (parent.root != null) {
            this.root = parent.root;
            this.mob = parent.mob;
            this.blackboard = parent.blackboard;
            this.onRootSet(this.root, this.mob, this.blackboard);
        }
    }

    public T mob() {
        return this.mob;
    }

    public Blackboard<T> getBlackboard() {
        return this.blackboard;
    }

    protected abstract void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3);

    public abstract void init(T var1, Blackboard<T> var2);

    public abstract AINodeResult tick(T var1, Blackboard<T> var2);

    public final void interruptRunning() {
        if (this.root != null) {
            this.root.interruptRunning();
        }
    }

    protected void onInterruptRunning(T mob, Blackboard<T> blackboard) {
        this.lastResult = null;
    }

    protected void onForceSetRunning(AINode<T> node) {
    }

    private void forceSetRunning(AINode<T> node) {
        if (this.parent != null && this.parent != this) {
            super.forceSetRunning(node);
            this.onForceSetRunning(node);
        }
    }

    public final void forceSetRunning() {
        this.forceSetRunning(this);
    }

    public Iterable<AINode<T>> debugChildren() {
        return Collections.emptyList();
    }

    public void addDebugTooltips(ListGameTooltips tooltips) {
    }

    public void addDebugActions(SelectionFloatMenu menu) {
    }
}

