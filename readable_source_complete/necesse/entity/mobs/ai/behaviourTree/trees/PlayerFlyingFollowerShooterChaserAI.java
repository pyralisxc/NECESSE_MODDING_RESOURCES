/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public abstract class PlayerFlyingFollowerShooterChaserAI<T extends Mob>
extends SelectorAINode<T> {
    public PlayerFlyingFollowerShooterChaserAI(int searchDistance, CooldownAttackTargetAINode.CooldownTimer cooldownTimer, int shootCooldown, int shootDistance, int teleportDistance, int stoppingDistance) {
        SequenceAINode chaserSequence = new SequenceAINode();
        chaserSequence.addChild(new FollowerBaseSetterAINode());
        chaserSequence.addChild(new FollowerFocusTargetSetterAINode());
        final SummonTargetFinderAINode targetFinder = new SummonTargetFinderAINode(searchDistance);
        chaserSequence.addChild(targetFinder);
        chaserSequence.addChild(new FlyingFollowerAINode<T>(-1, -1){

            @Override
            public Mob getFollowingMob(T mob) {
                return this.getBlackboard().getObject(Mob.class, targetFinder.currentTargetKey);
            }
        });
        chaserSequence.addChild(new CooldownAttackTargetAINode<T>(cooldownTimer, shootCooldown, shootDistance){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return PlayerFlyingFollowerShooterChaserAI.this.shootAtTarget(mob, target);
            }
        });
        this.addChild(chaserSequence);
        this.addChild(new PlayerFlyingFollowerAINode(teleportDistance, stoppingDistance));
    }

    protected abstract boolean shootAtTarget(T var1, Mob var2);
}

