/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanInteractingAINode<T extends HumanMob>
extends AINode<T> {
    protected boolean lastInteractedWith = false;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (mob.isBeingInteractedWith()) {
            if (((HumanMob)mob).objectUser != null) {
                ((HumanMob)mob).objectUser.stopUsing();
            }
            blackboard.mover.stopMoving((Mob)mob);
            this.lastInteractedWith = true;
            return AINodeResult.SUCCESS;
        }
        if (this.lastInteractedWith) {
            this.lastInteractedWith = false;
            blackboard.submitEvent("resetPathTime", new AIEvent());
        }
        return AINodeResult.FAILURE;
    }
}

