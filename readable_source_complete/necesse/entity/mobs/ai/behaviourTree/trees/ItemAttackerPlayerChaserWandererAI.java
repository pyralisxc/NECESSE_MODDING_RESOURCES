/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.util.function.Supplier;
import necesse.entity.Entity;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerPlayerChaserAI;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class ItemAttackerPlayerChaserWandererAI<T extends ItemAttackerMob>
extends SelectorAINode<T> {
    public final EscapeAINode<T> escapeAINode;
    public final ItemAttackerPlayerChaserAI<T> itemAttackerPlayerChaserAI;
    public final WandererAINode<T> wandererAINode;

    public ItemAttackerPlayerChaserWandererAI(final Supplier<Boolean> shouldEscape, int searchDistance, InventoryItem defaultAttackItem, int wanderFrequency) {
        this.escapeAINode = new EscapeAINode<T>(){

            @Override
            public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                if (((ItemAttackerMob)mob).isHostile && !((ItemAttackerMob)mob).isSummoned && ((Entity)mob).getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING).booleanValue()) {
                    return true;
                }
                return shouldEscape != null && (Boolean)shouldEscape.get() != false;
            }
        };
        this.addChild(this.escapeAINode);
        this.itemAttackerPlayerChaserAI = new ItemAttackerPlayerChaserAI(searchDistance, defaultAttackItem);
        this.addChild(this.itemAttackerPlayerChaserAI);
        this.wandererAINode = new WandererAINode(wanderFrequency);
        this.addChild(this.wandererAINode);
    }
}

