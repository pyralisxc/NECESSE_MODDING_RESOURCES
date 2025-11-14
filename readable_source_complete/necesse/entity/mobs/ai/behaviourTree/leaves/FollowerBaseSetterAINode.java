/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class FollowerBaseSetterAINode<T extends Mob>
extends AINode<T> {
    public String baseKey = "mobBase";

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        blackboard.put(this.baseKey, null);
        Mob followingMob = ((Mob)mob).getFollowingMob();
        if (followingMob != null) {
            blackboard.put(this.baseKey, new Point(followingMob.getX(), followingMob.getY()));
        }
        return AINodeResult.SUCCESS;
    }
}

