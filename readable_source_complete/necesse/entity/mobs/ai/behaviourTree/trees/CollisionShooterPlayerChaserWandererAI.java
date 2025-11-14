/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.util.function.Supplier;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public abstract class CollisionShooterPlayerChaserWandererAI<T extends Mob>
extends SelectorAINode<T> {
    public CollisionShooterPlayerChaserWandererAI(final Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, CooldownAttackTargetAINode.CooldownTimer cooldownTimer, int shootCooldown, int shootDistance, int wanderFrequency) {
        this.addChild(new EscapeAINode<T>(){

            @Override
            public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                if (((Mob)mob).isHostile && !((Mob)mob).isSummoned && ((Entity)mob).getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING).booleanValue()) {
                    return true;
                }
                return shouldEscape != null && (Boolean)shouldEscape.get() != false;
            }
        });
        CollisionPlayerChaserAI chaser = new CollisionPlayerChaserAI(searchDistance, damage, knockback);
        chaser.addChild(new CooldownAttackTargetAINode<T>(cooldownTimer, shootCooldown, shootDistance){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return CollisionShooterPlayerChaserWandererAI.this.shootAtTarget(mob, target);
            }
        });
        this.addChild(chaser);
        this.addChild(new WandererAINode(wanderFrequency));
    }

    public abstract boolean shootAtTarget(T var1, Mob var2);
}

