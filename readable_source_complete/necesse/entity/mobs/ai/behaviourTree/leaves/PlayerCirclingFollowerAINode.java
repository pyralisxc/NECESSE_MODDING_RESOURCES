/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.CirclingFollowerAINode;

public class PlayerCirclingFollowerAINode<T extends Mob>
extends CirclingFollowerAINode<T> {
    public PlayerCirclingFollowerAINode(int teleportDistance, int circlingRange) {
        super(teleportDistance, circlingRange);
    }

    @Override
    public Mob getFollowingMob(T mob) {
        return ((Mob)mob).getFollowingMob();
    }
}

