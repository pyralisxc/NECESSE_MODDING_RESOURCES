/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.util.Comparator;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidClawGroundShatterStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public boolean waitForClaw;
    protected TheVoidClawMob clawMob;

    public TheVoidClawGroundShatterStage(boolean waitForClaw) {
        this.waitForClaw = waitForClaw;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.clawMob == null || this.clawMob.isIdle() || this.clawMob.removed()) {
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, "currentTarget");
        if (target == null) {
            return;
        }
        this.clawMob = ((TheVoidMob)mob).spawnedClaws.stream().filter(TheVoidClawMob::isIdle).min(Comparator.comparingDouble(c -> c.getDistance(target))).orElse(null);
        if (this.clawMob != null) {
            this.clawMob.flyToTargetAndSlam(target, 2000, 500, 2000, 2000, true);
        }
        if (!this.waitForClaw) {
            this.clawMob = null;
        }
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

