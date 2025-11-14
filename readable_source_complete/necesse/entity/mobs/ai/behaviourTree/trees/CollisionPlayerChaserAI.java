/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public class CollisionPlayerChaserAI<T extends Mob>
extends CollisionChaserAI<T> {
    public CollisionPlayerChaserAI(int searchDistance, GameDamage damage, int knockback) {
        super(searchDistance, damage, knockback);
    }

    @Override
    public GameAreaStream<Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
        return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
    }
}

