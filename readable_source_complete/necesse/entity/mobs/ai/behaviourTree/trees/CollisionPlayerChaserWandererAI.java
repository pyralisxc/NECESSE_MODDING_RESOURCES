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
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class CollisionPlayerChaserWandererAI<T extends Mob>
extends SelectorAINode<T> {
    public final EscapeAINode<T> escapeAINode;
    public final CollisionPlayerChaserAI<T> collisionPlayerChaserAI;
    public final WandererAINode<T> wandererAINode;

    public CollisionPlayerChaserWandererAI(final Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, int wanderFrequency) {
        this.escapeAINode = new EscapeAINode<T>(){

            @Override
            public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                if (((Mob)mob).isHostile && !((Mob)mob).isSummoned && ((Entity)mob).getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING).booleanValue()) {
                    return true;
                }
                return shouldEscape != null && (Boolean)shouldEscape.get() != false;
            }
        };
        this.addChild(this.escapeAINode);
        this.collisionPlayerChaserAI = new CollisionPlayerChaserAI<T>(searchDistance, damage, knockback){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return CollisionPlayerChaserWandererAI.this.attackTarget(mob, target);
            }
        };
        this.addChild(this.collisionPlayerChaserAI);
        this.wandererAINode = new WandererAINode(wanderFrequency);
        this.addChild(this.wandererAINode);
    }

    public boolean attackTarget(T mob, Mob target) {
        return CollisionChaserAINode.simpleAttack(mob, target, this.collisionPlayerChaserAI.damage, this.collisionPlayerChaserAI.knockback);
    }
}

