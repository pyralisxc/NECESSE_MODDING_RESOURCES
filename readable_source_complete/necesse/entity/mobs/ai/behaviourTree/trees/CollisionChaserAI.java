/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.SucceederAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.LooseTargetTimerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public abstract class CollisionChaserAI<T extends Mob>
extends SequenceAINode<T> {
    public GameDamage damage;
    public int knockback;
    public final LooseTargetTimerAINode<T> looseTargetTimerAINode;
    public final TargetFinderAINode<T> targetFinderAINode;
    public final CollisionChaserAINode<T> collisionChaserAINode;

    public CollisionChaserAI(int searchDistance, GameDamage damage, int knockback) {
        this.damage = damage;
        this.knockback = knockback;
        this.looseTargetTimerAINode = new LooseTargetTimerAINode();
        this.addChild(new SucceederAINode(this.looseTargetTimerAINode));
        this.targetFinderAINode = new TargetFinderAINode<T>(searchDistance){

            @Override
            public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                return CollisionChaserAI.this.streamPossibleTargets(mob, base, distance);
            }
        };
        this.addChild(this.targetFinderAINode);
        this.collisionChaserAINode = new CollisionChaserAINode<T>(){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return CollisionChaserAI.this.attackTarget(mob, target);
            }
        };
        this.addChild(this.collisionChaserAINode);
    }

    public boolean attackTarget(T mob, Mob target) {
        return CollisionChaserAINode.simpleAttack(mob, target, this.damage, this.knockback);
    }

    public abstract GameAreaStream<Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3);
}

