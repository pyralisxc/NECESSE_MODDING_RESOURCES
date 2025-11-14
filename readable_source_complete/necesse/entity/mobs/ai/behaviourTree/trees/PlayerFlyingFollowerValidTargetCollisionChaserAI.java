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
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;

public abstract class PlayerFlyingFollowerValidTargetCollisionChaserAI<T extends Mob>
extends SelectorAINode<T> {
    public PlayerFlyingFollowerValidTargetCollisionChaserAI(int searchDistance, final GameDamage damage, final int knockback, int hitCooldown, int teleportDistance, int stoppingDistance) {
        SequenceAINode chaserSequence = new SequenceAINode();
        chaserSequence.addChild(new FollowerBaseSetterAINode());
        chaserSequence.addChild(new FollowerFocusTargetSetterAINode());
        chaserSequence.addChild(new SummonTargetFinderAINode<T>(searchDistance){

            @Override
            public boolean isValidTarget(T mob, ItemAttackerMob owner, Mob target, Mob focusTarget) {
                return super.isValidTarget(mob, owner, target, focusTarget) && PlayerFlyingFollowerValidTargetCollisionChaserAI.this.isValidTarget(mob, owner, target);
            }
        });
        CollisionChaserAINode chaser = new CollisionChaserAINode<T>(){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return CollisionChaserAINode.simpleAttack(mob, target, damage, knockback);
            }
        };
        chaser.hitCooldowns.hitCooldown = hitCooldown;
        chaser.attackMoveCooldown = 0;
        chaserSequence.addChild(chaser);
        this.addChild(chaserSequence);
        this.addChild(new PlayerFlyingFollowerAINode(teleportDistance, stoppingDistance));
    }

    public abstract boolean isValidTarget(T var1, ItemAttackerMob var2, Mob var3);
}

