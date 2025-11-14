/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;

public class PlayerFollowerAINode<T extends Mob>
extends FollowerAINode<T> {
    public PlayerFollowerAINode(int teleportDistance, int stoppingDistance) {
        super(teleportDistance, stoppingDistance);
    }

    @Override
    public Mob getFollowingMob(T mob) {
        return ((Mob)mob).getFollowingMob();
    }
}

