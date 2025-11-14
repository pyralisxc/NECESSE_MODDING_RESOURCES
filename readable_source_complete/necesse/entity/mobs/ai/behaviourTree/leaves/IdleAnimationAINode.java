/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class IdleAnimationAINode<T extends Mob>
extends AINode<T> {
    public int animIn;

    public IdleAnimationAINode() {
        this.resetIdleAnimationCooldown();
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (!((Mob)mob).isAccelerating() && !blackboard.mover.isMoving()) {
            --this.animIn;
            if (this.animIn <= 0) {
                this.runIdleAnimation(mob);
                this.resetIdleAnimationCooldown();
            }
        }
        return AINodeResult.FAILURE;
    }

    public void resetIdleAnimationCooldown() {
        this.animIn = this.getIdleAnimationCooldown(GameRandom.globalRandom);
    }

    public abstract int getIdleAnimationCooldown(GameRandom var1);

    public abstract void runIdleAnimation(T var1);
}

