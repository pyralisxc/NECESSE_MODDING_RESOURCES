/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree;

import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitCalculatedEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;

public class BehaviourTreeAI<T extends Mob> {
    public final T mob;
    public final AINode<T> tree;
    public final Blackboard<T> blackboard;

    public BehaviourTreeAI(T mob, AINode<T> tree) {
        this(mob, tree, new AIMover());
    }

    public BehaviourTreeAI(T mob, AINode<T> tree, AIMover mover) {
        this.mob = mob;
        this.tree = tree;
        this.blackboard = new Blackboard(mover);
        tree.makeRoot(mob, this.blackboard);
    }

    public void tick() {
        if (((Entity)this.mob).getWorldSettings().disableMobAI) {
            ((Mob)this.mob).stopMoving();
            return;
        }
        this.blackboard.globalTickEvents.submitEvent(new AIEvent());
        this.tree.init(this.mob, this.blackboard);
        this.tree.lastResult = this.tree.tick(this.mob, this.blackboard);
        this.blackboard.mover.tick((Mob)this.mob);
        this.blackboard.clearLatestEvents();
    }

    public void resetStuck() {
        this.blackboard.mover.resetStuck();
    }

    public void beforeHit(MobBeforeHitEvent event) {
        this.blackboard.beforeHitEvents.submitEvent(new AIBeforeHitEvent(event));
    }

    public void beforeHitCalculated(MobBeforeHitCalculatedEvent event) {
        this.blackboard.beforeHitCalculatedEvents.submitEvent(new AIBeforeHitCalculatedEvent(event));
    }

    public void wasHit(MobWasHitEvent event) {
        this.blackboard.wasHitEvents.submitEvent(new AIWasHitEvent(event));
    }

    public void onUnloading() {
        this.blackboard.onUnloadingEvents.submitEvent(new AIEvent());
    }

    public void isRemoved() {
        this.blackboard.removedEvents.submitEvent(new AIEvent());
    }
}

