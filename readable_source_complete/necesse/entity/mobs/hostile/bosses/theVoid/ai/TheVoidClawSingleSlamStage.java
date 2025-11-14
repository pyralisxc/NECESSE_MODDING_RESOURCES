/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.util.Comparator;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidClawSingleSlamStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, "currentTarget");
        if (target == null) {
            return;
        }
        TheVoidClawMob claw = ((TheVoidMob)mob).spawnedClaws.stream().filter(TheVoidClawMob::isIdle).min(Comparator.comparingDouble(c -> c.getDistance(target))).orElse(null);
        if (claw != null) {
            float offsetX = GameMath.limit(target.dx * 10.0f, -50.0f, 50.0f);
            float offsetY = GameMath.limit(target.dy * 10.0f, -50.0f, 50.0f);
            claw.flyToTargetAndSlam(target, offsetX, offsetY, 2000, 300, 1000, 1000, false);
        }
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

