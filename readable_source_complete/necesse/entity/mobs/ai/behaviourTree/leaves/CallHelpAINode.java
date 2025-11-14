/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

public class CallHelpAINode<T extends Mob>
extends AINode<T> {
    public String currentTargetKey = "currentTarget";
    public String chaserTargetKey = "chaserTarget";
    public final String eventType;
    public int tileRange;
    public int callCooldown;
    public boolean recursive = false;
    public Mob calledTarget;
    public long lastCallTime;

    public CallHelpAINode(String eventType, int tileRange, int callCooldown) {
        this.eventType = eventType;
        this.tileRange = tileRange;
        this.callCooldown = callCooldown;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onEvent(this.eventType, e -> {
            Mob target;
            if (e instanceof AICallHelpEvent && (target = blackboard.getObject(Mob.class, this.chaserTargetKey)) == null) {
                Mob newTarget;
                this.calledTarget = newTarget = ((AICallHelpEvent)e).target;
                this.lastCallTime = mob.getWorldEntity().getTime();
                blackboard.put(this.currentTargetKey, newTarget);
            }
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob target;
        if (this.calledTarget != null && (!this.calledTarget.isSamePlace((Entity)mob) || this.calledTarget.removed())) {
            this.calledTarget = null;
        }
        if ((target = blackboard.getObject(Mob.class, this.chaserTargetKey)) != null) {
            if ((this.recursive || target != this.calledTarget) && this.lastCallTime + (long)this.callCooldown < ((Entity)mob).getWorldEntity().getTime()) {
                ((Entity)mob).getLevel().entityManager.mobs.getInRegionByTileRange(((Entity)mob).getTileX(), ((Entity)mob).getTileY(), this.tileRange).forEach(m -> m.ai.blackboard.submitEvent(this.eventType, new AICallHelpEvent(target)));
                this.lastCallTime = ((Entity)mob).getWorldEntity().getTime();
                return AINodeResult.SUCCESS;
            }
        } else {
            this.calledTarget = null;
        }
        return AINodeResult.FAILURE;
    }

    public static class AICallHelpEvent
    extends AIEvent {
        public final Mob target;

        public AICallHelpEvent(Mob target) {
            this.target = target;
        }
    }
}

