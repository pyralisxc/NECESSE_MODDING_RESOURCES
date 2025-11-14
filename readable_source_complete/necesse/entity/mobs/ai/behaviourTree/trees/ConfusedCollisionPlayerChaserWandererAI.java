/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.geom.Point2D;
import java.util.function.Supplier;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ConfusedWandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;

public class ConfusedCollisionPlayerChaserWandererAI<T extends Mob>
extends CollisionPlayerChaserWandererAI<T> {
    public final ConfusedWandererAINode<T> confusedWandererNode = new ConfusedWandererAINode();

    public ConfusedCollisionPlayerChaserWandererAI(Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, int wanderFrequency) {
        super(shouldEscape, searchDistance, damage, knockback, wanderFrequency);
        this.addChildFirst(this.confusedWandererNode);
    }

    protected int getRandomConfuseTime() {
        if (GameRandom.globalRandom.getChance(0.1f)) {
            return GameRandom.globalRandom.getIntBetween(3000, 5000);
        }
        return GameRandom.globalRandom.getIntBetween(500, 1000);
    }

    @Override
    public boolean attackTarget(T mob, Mob target) {
        boolean success = super.attackTarget(mob, target);
        if (success && GameRandom.globalRandom.getChance(0.5f)) {
            Point2D.Float attackDir = GameMath.normalize(target.x - ((Mob)mob).x, target.y - ((Mob)mob).y);
            float attackAngle = GameMath.getAngle(attackDir);
            float runAwayAngle = GameRandom.globalRandom.nextBoolean() ? GameRandom.globalRandom.getFloatBetween(attackAngle - 90.0f, attackAngle - 110.0f) : GameRandom.globalRandom.getFloatBetween(attackAngle + 90.0f, attackAngle + 110.0f);
            runAwayAngle = GameMath.fixAngle(runAwayAngle);
            Point2D.Float runAwayDir = GameMath.getAngleDir(runAwayAngle);
            int confuseTime = this.getRandomConfuseTime();
            this.getBlackboard().submitEvent("confuseWander", new ConfuseWanderAIEvent(confuseTime, runAwayDir));
        }
        return success;
    }
}

