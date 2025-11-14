/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;

public class ItemAttackerPlayerChaserAI<T extends ItemAttackerMob>
extends ItemAttackerChaserAI<T> {
    public ItemAttackerPlayerChaserAI(int searchDistance, InventoryItem defaultAttackItem) {
        super(searchDistance, defaultAttackItem);
    }

    @Override
    public GameAreaStream<Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
        return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
    }
}

