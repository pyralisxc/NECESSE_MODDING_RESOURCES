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
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public class PlayerFollowerCollisionChaserAI<T extends Mob>
extends SelectorAINode<T> {
    public GameDamage damage;
    public int knockback;

    public PlayerFollowerCollisionChaserAI(int searchDistance, GameDamage damage, int knockback, int hitCooldown, int teleportDistance, int stoppingDistance) {
        this.damage = damage;
        this.knockback = knockback;
        SequenceAINode chaserSequence = new SequenceAINode();
        chaserSequence.addChild(new FollowerBaseSetterAINode());
        chaserSequence.addChild(new FollowerFocusTargetSetterAINode<T>(){

            @Override
            public Mob getCustomFocus() {
                return PlayerFollowerCollisionChaserAI.this.getCustomFocus();
            }
        });
        chaserSequence.addChild(new SummonTargetFinderAINode(searchDistance));
        CollisionChaserAINode chaser = new CollisionChaserAINode<T>(){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return PlayerFollowerCollisionChaserAI.this.attackTarget(mob, target);
            }
        };
        chaser.hitCooldowns.hitCooldown = hitCooldown;
        chaser.attackMoveCooldown = 0;
        chaserSequence.addChild(chaser);
        this.addChild(chaserSequence);
        this.addChild(new PlayerFollowerAINode(teleportDistance, stoppingDistance));
    }

    public boolean attackTarget(T mob, Mob target) {
        return CollisionChaserAINode.simpleAttack(mob, target, this.damage, this.knockback);
    }

    public Mob getCustomFocus() {
        return null;
    }
}

