/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.geom.Point2D;
import java.util.function.Supplier;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ConfusedWandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;

public abstract class ConfusedPlayerChaserWandererAI<T extends AttackAnimMob>
extends PlayerChaserWandererAI<T> {
    public final ConfusedWandererAINode<T> confusedWandererNode;
    protected boolean wanderAfterAttack = false;

    public ConfusedPlayerChaserWandererAI(Supplier<Boolean> shouldEscape, int searchDistance, int shootDistance, int wanderFrequency, boolean smartPositioning, boolean changePositionOnHit) {
        super(shouldEscape, searchDistance, shootDistance, wanderFrequency, smartPositioning, changePositionOnHit);
        this.addChildFirst(new AINode<T>(){

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                blackboard.onEvent("wanderAfterAttack", e -> {
                    ConfusedPlayerChaserWandererAI.this.wanderAfterAttack = true;
                });
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (ConfusedPlayerChaserWandererAI.this.wanderAfterAttack && !((AttackAnimMob)mob).isAttacking) {
                    ConfusedPlayerChaserWandererAI.this.wanderAfterAttack = false;
                    ConfusedPlayerChaserWandererAI.this.runWanderAfterAttack(mob, blackboard);
                }
                return AINodeResult.FAILURE;
            }
        });
        this.confusedWandererNode = new ConfusedWandererAINode();
        this.addChildFirst(this.confusedWandererNode);
    }

    protected int getRandomConfuseTime() {
        if (GameRandom.globalRandom.getChance(0.1f)) {
            return GameRandom.globalRandom.getIntBetween(2000, 4000);
        }
        return GameRandom.globalRandom.getIntBetween(1000, 1500);
    }

    public void runWanderAfterAttack(T mob, Blackboard<T> blackboard) {
        if (((AttackAnimMob)mob).attackDir != null) {
            float attackAngle = GameMath.getAngle(((AttackAnimMob)mob).attackDir);
            float runAwayAngle = GameRandom.globalRandom.nextBoolean() ? GameRandom.globalRandom.getFloatBetween(attackAngle - 90.0f, attackAngle - 110.0f) : GameRandom.globalRandom.getFloatBetween(attackAngle + 90.0f, attackAngle + 110.0f);
            runAwayAngle = GameMath.fixAngle(runAwayAngle);
            Point2D.Float runAwayDir = GameMath.getAngleDir(runAwayAngle);
            int confuseTime = this.getRandomConfuseTime();
            blackboard.submitEvent("confuseWander", new ConfuseWanderAIEvent(confuseTime, runAwayDir));
        }
    }
}

