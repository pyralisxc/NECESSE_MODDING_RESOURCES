/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.geom.Point2D;
import java.util.function.Supplier;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ConfusedWandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerPlayerChaserWandererAI;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;

public class ConfusedItemAttackerPlayerChaserWandererAI<T extends ItemAttackerMob>
extends ItemAttackerPlayerChaserWandererAI<T> {
    public final ConfusedWandererAINode<T> confusedWandererNode;
    protected boolean wanderAfterAttack = false;

    public ConfusedItemAttackerPlayerChaserWandererAI(Supplier<Boolean> shouldEscape, int searchDistance, InventoryItem defaultAttackItem, int wanderFrequency) {
        super(shouldEscape, searchDistance, defaultAttackItem, wanderFrequency);
        this.addChildFirst(new AINode<T>(){

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                blackboard.onEvent("wanderAfterAttack", e -> {
                    ConfusedItemAttackerPlayerChaserWandererAI.this.wanderAfterAttack = true;
                });
                blackboard.onEvent("itemAttack", e -> ConfusedItemAttackerPlayerChaserWandererAI.this.onHasAttacked());
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                ((ItemAttackerMob)mob).getAttackAnimProgress();
                if (ConfusedItemAttackerPlayerChaserWandererAI.this.wanderAfterAttack && !((ItemAttackerMob)mob).isAttacking) {
                    ConfusedItemAttackerPlayerChaserWandererAI.this.wanderAfterAttack = false;
                    ConfusedItemAttackerPlayerChaserWandererAI.this.runWanderAfterAttack(mob, blackboard);
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.FAILURE;
            }
        });
        this.confusedWandererNode = new ConfusedWandererAINode();
        this.addChildFirst(this.confusedWandererNode);
    }

    protected void onHasAttacked() {
        this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
    }

    protected int getRandomConfuseTime() {
        if (GameRandom.globalRandom.getChance(0.1f)) {
            return GameRandom.globalRandom.getIntBetween(2000, 4000);
        }
        return GameRandom.globalRandom.getIntBetween(1000, 1500);
    }

    public void runWanderAfterAttack(T mob, Blackboard<T> blackboard) {
        if (((ItemAttackerMob)mob).attackDir != null) {
            float attackAngle = GameMath.getAngle(((ItemAttackerMob)mob).attackDir);
            float runAwayAngle = GameRandom.globalRandom.nextBoolean() ? GameRandom.globalRandom.getFloatBetween(attackAngle - 90.0f, attackAngle - 110.0f) : GameRandom.globalRandom.getFloatBetween(attackAngle + 90.0f, attackAngle + 110.0f);
            runAwayAngle = GameMath.fixAngle(runAwayAngle);
            Point2D.Float runAwayDir = GameMath.getAngleDir(runAwayAngle);
            int confuseTime = this.getRandomConfuseTime();
            blackboard.submitEvent("confuseWander", new ConfuseWanderAIEvent(confuseTime, runAwayDir));
        }
    }
}

