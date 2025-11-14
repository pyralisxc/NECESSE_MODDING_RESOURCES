/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerCirclingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public class PlayerCirclingFollowerCollisionChaserAI<T extends Mob>
extends SelectorAINode<T> {
    public PlayerCirclingFollowerCollisionChaserAI(int searchDistance, final GameDamage damage, final int knockback, int teleportDistance, int circlingRange) {
        SequenceAINode chaserSequence = new SequenceAINode();
        chaserSequence.addChild(new FollowerBaseSetterAINode());
        chaserSequence.addChild(new FollowerFocusTargetSetterAINode());
        chaserSequence.addChild(new SummonTargetFinderAINode(searchDistance));
        CollisionChaserAINode chaser = new CollisionChaserAINode<T>(){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return CollisionChaserAINode.simpleAttack(mob, target, damage, knockback);
            }
        };
        chaser.attackMoveCooldown = 0;
        chaserSequence.addChild(chaser);
        this.addChild(chaserSequence);
        this.addChild(new PlayerCirclingFollowerAINode(teleportDistance, circlingRange));
    }
}

