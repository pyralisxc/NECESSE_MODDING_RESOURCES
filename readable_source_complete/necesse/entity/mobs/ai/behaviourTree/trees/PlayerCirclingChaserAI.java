/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public class PlayerCirclingChaserAI<T extends Mob>
extends SequenceAINode<T> {
    public PlayerCirclingChaserAI(int searchDistance, int circlingRange, int nextAngleOffset) {
        this.addChild(new TargetFinderAINode<T>(searchDistance){

            @Override
            public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
            }
        });
        this.addChild(new CirclingChaserAINode(circlingRange, nextAngleOffset));
    }
}

