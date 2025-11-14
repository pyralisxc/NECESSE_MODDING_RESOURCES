/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.SucceederAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.LooseTargetTimerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;

public abstract class ItemAttackerChaserAI<T extends ItemAttackerMob>
extends SequenceAINode<T> {
    public final LooseTargetTimerAINode<T> looseTargetTimerAINode = new LooseTargetTimerAINode();
    public final TargetFinderAINode<T> targetFinderAINode;
    public final ItemAttackerChaserAINode<T> itemAttackerChaserAINode;

    public ItemAttackerChaserAI(int searchDistance, InventoryItem defaultAttackItem) {
        this.addChild(new SucceederAINode(this.looseTargetTimerAINode));
        this.targetFinderAINode = new TargetFinderAINode<T>(searchDistance){

            @Override
            public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                return ItemAttackerChaserAI.this.streamPossibleTargets(mob, base, distance);
            }
        };
        this.addChild(this.targetFinderAINode);
        this.itemAttackerChaserAINode = new ItemAttackerChaserAINode(defaultAttackItem);
        this.addChild(this.itemAttackerChaserAINode);
    }

    public abstract GameAreaStream<Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3);
}

