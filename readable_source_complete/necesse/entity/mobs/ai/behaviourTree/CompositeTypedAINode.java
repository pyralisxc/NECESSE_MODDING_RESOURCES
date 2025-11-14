/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;

public abstract class CompositeTypedAINode<T extends Mob, C extends AINode<T>>
extends AINode<T> {
    protected ArrayList<C> children = new ArrayList();
    protected C runningNode;

    public CompositeTypedAINode<T, C> addChild(C child) {
        this.children.add(child);
        ((AINode)child).setChildRoot(this);
        return this;
    }

    public CompositeTypedAINode<T, C> addChildAfter(C targetElement, C child, boolean addIfNotFound) {
        ListIterator<C> li = this.children.listIterator();
        while (li.hasNext()) {
            if (!Objects.equals(li.next(), targetElement)) continue;
            this.children.add(li.nextIndex(), child);
            ((AINode)child).setChildRoot(this);
            return this;
        }
        if (!addIfNotFound) {
            throw new IllegalArgumentException("Node to place child after is not part of children");
        }
        this.children.add(child);
        ((AINode)child).setChildRoot(this);
        return this;
    }

    public final CompositeTypedAINode<T, C> addChildAfter(C targetElement, C child) {
        return this.addChildAfter(targetElement, child, false);
    }

    public CompositeTypedAINode<T, C> addChildBefore(C targetElement, C child, boolean addIfNotFound) {
        ListIterator<C> li = this.children.listIterator();
        while (li.hasNext()) {
            if (!Objects.equals(li.next(), targetElement)) continue;
            int previousIndex = li.previousIndex();
            if (previousIndex == -1) {
                this.children.add(0, child);
            } else {
                this.children.add(previousIndex, child);
            }
            ((AINode)child).setChildRoot(this);
            return this;
        }
        if (!addIfNotFound) {
            throw new IllegalArgumentException("Node to place child after is not part of children");
        }
        this.children.add(child);
        ((AINode)child).setChildRoot(this);
        return this;
    }

    public final CompositeTypedAINode<T, C> addChildBefore(C targetElement, C child) {
        return this.addChildBefore(targetElement, child, false);
    }

    public CompositeTypedAINode<T, C> addChildFirst(C child) {
        this.children.add(0, child);
        ((AINode)child).setChildRoot(this);
        return this;
    }

    @Override
    public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        for (AINode child : this.children) {
            child.setChildRoot(this);
        }
    }

    @Override
    public void onInterruptRunning(T mob, Blackboard<T> blackboard) {
        super.onInterruptRunning(mob, blackboard);
        this.runningNode = null;
        for (AINode child : this.children) {
            child.onInterruptRunning(mob, blackboard);
        }
    }

    @Override
    protected void onForceSetRunning(AINode<T> node) {
        if (this.children.contains(node)) {
            this.runningNode = node;
        }
        for (AINode child : this.children) {
            child.onForceSetRunning(node);
        }
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.runningNode == null) {
            for (AINode node : this.children) {
                node.lastResult = null;
                node.init(mob, blackboard);
            }
        } else {
            ((AINode)this.runningNode).lastResult = ((AINode)this.runningNode).tick(mob, blackboard);
            AINodeResult result = ((AINode)this.runningNode).lastResult;
            if (result == AINodeResult.RUNNING) {
                return AINodeResult.RUNNING;
            }
            int fromIndex = this.children.indexOf(this.runningNode) + 1;
            C lastRunningChild = this.runningNode;
            this.runningNode = null;
            return this.tickChildren(lastRunningChild, result, this.children.subList(fromIndex, this.children.size()), mob, blackboard);
        }
        return this.tickChildren(null, null, this.children, mob, blackboard);
    }

    protected abstract AINodeResult tickChildren(C var1, AINodeResult var2, Iterable<C> var3, T var4, Blackboard<T> var5);

    @Override
    public Iterable<AINode<T>> debugChildren() {
        return this.children;
    }

    @Override
    public void addDebugActions(SelectionFloatMenu menu) {
        menu.add("Clear running node", () -> {
            this.runningNode = null;
            menu.remove();
        });
    }
}

