/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.CompositeAINode;
import necesse.entity.mobs.ai.behaviourTree.CompositeTypedAINode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageSkipTo;

public class AttackStageManagerNode<T extends Mob>
extends CompositeAINode<T>
implements AttackStageInterface<T> {
    public AINodeResult returnResult;
    public boolean allowSkippingBack = true;
    private boolean hasSkipToObjects = false;

    public AttackStageManagerNode(AINodeResult returnResult) {
        this.returnResult = returnResult;
    }

    public AttackStageManagerNode() {
        this(AINodeResult.SUCCESS);
    }

    @Override
    public CompositeTypedAINode<T, AINode<T>> addChild(AINode<T> child) {
        if (child instanceof AttackStageSkipTo) {
            this.hasSkipToObjects = true;
        }
        return super.addChild(child);
    }

    @Override
    public CompositeTypedAINode<T, AINode<T>> addChildAfter(AINode<T> targetElement, AINode<T> child, boolean addIfNotFound) {
        if (child instanceof AttackStageSkipTo) {
            this.hasSkipToObjects = true;
        }
        return super.addChildAfter(targetElement, child, addIfNotFound);
    }

    @Override
    public CompositeTypedAINode<T, AINode<T>> addChildBefore(AINode<T> targetElement, AINode<T> child, boolean addIfNotFound) {
        if (child instanceof AttackStageSkipTo) {
            this.hasSkipToObjects = true;
        }
        return super.addChildBefore(targetElement, child, addIfNotFound);
    }

    @Override
    public CompositeTypedAINode<T, AINode<T>> addChildFirst(AINode<T> child) {
        if (child instanceof AttackStageSkipTo) {
            this.hasSkipToObjects = true;
        }
        return super.addChildFirst(child);
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        block8: {
            if (!this.hasSkipToObjects) break block8;
            if (this.allowSkippingBack) {
                int startIndex = this.runningNode == null ? this.children.size() - 1 : this.children.indexOf(this.runningNode);
                for (int notIndex = 1; notIndex < this.children.size(); ++notIndex) {
                    AttackStageSkipTo skipTo;
                    int i = Math.floorMod(startIndex - notIndex, this.children.size());
                    if (!(this.children.get(i) instanceof AttackStageSkipTo) || !(skipTo = (AttackStageSkipTo)this.children.get(i)).shouldSkipTo(mob, this.runningNode == this.children.get(Math.floorMod(i - 1, this.children.size())))) continue;
                    if (this.runningNode != null) {
                        this.runningNode.lastResult = null;
                        if (this.runningNode instanceof AttackStageInterface) {
                            ((AttackStageInterface)((Object)this.runningNode)).onEnded(mob, blackboard);
                        }
                        this.onInterruptRunning(mob, blackboard);
                    }
                    this.runningNode = (AINode)this.children.get(i);
                    if (!(this.runningNode instanceof AttackStageInterface)) break;
                    ((AttackStageInterface)((Object)this.runningNode)).onStarted(mob, blackboard);
                    break;
                }
            } else {
                for (int i = this.children.size() - 1; i >= 0 && this.runningNode != this.children.get(i); --i) {
                    AttackStageSkipTo skipTo;
                    if (!(this.children.get(i) instanceof AttackStageSkipTo) || !(skipTo = (AttackStageSkipTo)this.children.get(i)).shouldSkipTo(mob, i > 0 && this.runningNode == this.children.get(i - 1))) continue;
                    if (this.runningNode != null) {
                        this.runningNode.lastResult = null;
                        if (this.runningNode instanceof AttackStageInterface) {
                            ((AttackStageInterface)((Object)this.runningNode)).onEnded(mob, blackboard);
                        }
                        this.onInterruptRunning(mob, blackboard);
                    }
                    this.runningNode = (AINode)this.children.get(i);
                    if (!(this.runningNode instanceof AttackStageInterface)) break;
                    ((AttackStageInterface)((Object)this.runningNode)).onStarted(mob, blackboard);
                    break;
                }
            }
        }
        return super.tick(mob, blackboard);
    }

    @Override
    protected AINodeResult tickChildren(AINode<T> lastRunningChild, AINodeResult runningChildResult, Iterable<AINode<T>> children, T mob, Blackboard<T> blackboard) {
        if (lastRunningChild instanceof AttackStageInterface) {
            ((AttackStageInterface)((Object)lastRunningChild)).onEnded(mob, blackboard);
        }
        for (AINode<T> child : children) {
            AINodeResult result;
            if (child instanceof AttackStageInterface) {
                ((AttackStageInterface)((Object)child)).onStarted(mob, blackboard);
            }
            if ((result = (child.lastResult = child.tick(mob, blackboard))) == AINodeResult.RUNNING) {
                this.runningNode = child;
                return AINodeResult.RUNNING;
            }
            if (!(child instanceof AttackStageInterface)) continue;
            ((AttackStageInterface)((Object)child)).onEnded(mob, blackboard);
        }
        return this.returnResult;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

