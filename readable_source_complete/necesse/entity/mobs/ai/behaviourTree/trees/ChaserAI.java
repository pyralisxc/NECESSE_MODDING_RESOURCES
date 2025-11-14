/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public abstract class ChaserAI<T extends Mob>
extends SequenceAINode<T> {
    public final TargetFinderAINode<T> targetFinderAINode;
    public final ChaserAINode<T> chaserAINode;

    public ChaserAI(int searchDistance, int shootDistance, boolean smartPositioning, boolean changePositionOnHit) {
        this.targetFinderAINode = new TargetFinderAINode<T>(searchDistance){

            @Override
            public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                return ChaserAI.this.streamPossibleTargets(mob, base, distance);
            }
        };
        this.addChild(this.targetFinderAINode);
        this.chaserAINode = new ChaserAINode<T>(shootDistance, smartPositioning, changePositionOnHit){

            @Override
            public boolean canHitTarget(T mob, float fromX, float fromY, Mob target) {
                return ChaserAI.this.canHitTarget(mob, fromX, fromY, target);
            }

            @Override
            public boolean attackTarget(T mob, Mob target) {
                return ChaserAI.this.attackTarget(mob, target);
            }
        };
        this.addChild(this.chaserAINode);
    }

    public abstract GameAreaStream<Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3);

    public boolean canHitTarget(T mob, float fromX, float fromY, Mob target) {
        return this.chaserAINode.canHitTarget(mob, fromX, fromY, target);
    }

    public abstract boolean attackTarget(T var1, Mob var2);
}

