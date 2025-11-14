/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;

public class FollowerFocusTargetSetterAINode<T extends Mob>
extends AINode<T> {
    public String focusTargetKey;

    public FollowerFocusTargetSetterAINode(String focusTargetKey) {
        this.focusTargetKey = focusTargetKey;
    }

    public FollowerFocusTargetSetterAINode() {
        this("focusTarget");
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob customFocus = this.getCustomFocus();
        if (customFocus != null) {
            blackboard.put(this.focusTargetKey, customFocus);
        } else {
            blackboard.put(this.focusTargetKey, null);
            ItemAttackerMob followingAttacker = ((Mob)mob).getFollowingItemAttacker();
            if (followingAttacker != null) {
                blackboard.put(this.focusTargetKey, followingAttacker.getSummonFocusMob());
            }
        }
        return AINodeResult.SUCCESS;
    }

    public Mob getCustomFocus() {
        return null;
    }
}

