/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public abstract class PlayerFollowerChaserOnlyAI<T extends Mob>
extends SelectorAINode<T> {
    public PlayerFollowerChaserOnlyAI(int searchDistance, int shootDistance, boolean smartPositioning, boolean changePositionOnHit, int teleportDistance, int stoppingDistance) {
        SequenceAINode chaserSequence = new SequenceAINode();
        chaserSequence.addChild(new FollowerBaseSetterAINode());
        chaserSequence.addChild(new FollowerFocusTargetSetterAINode());
        chaserSequence.addChild(new SummonTargetFinderAINode(searchDistance));
        ChaserAINode chaser = new ChaserAINode<T>(shootDistance, smartPositioning, changePositionOnHit){

            @Override
            public boolean canHitTarget(T mob, float fromX, float fromY, Mob target) {
                return PlayerFollowerChaserOnlyAI.this.canHitTarget(mob, fromX, fromY, target);
            }

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return PlayerFollowerChaserOnlyAI.this.attackTarget(mob, target);
            }
        };
        chaser.timeBeforeFirstAttack = 0;
        chaserSequence.addChild(chaser);
        this.addChild(chaserSequence);
    }

    public boolean canHitTarget(T mob, float fromX, float fromY, Mob target) {
        return ChaserAINode.hasLineOfSightToTarget(mob, fromX, fromY, target);
    }

    public abstract boolean attackTarget(T var1, Mob var2);
}

