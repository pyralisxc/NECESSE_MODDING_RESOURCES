/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.util.Comparator;
import necesse.entity.Entity;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidClawGroundAtPositionShatterStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public boolean waitForClaw;
    public int relativeHeadX;
    public int relativeHeadY;
    protected TheVoidClawMob clawMob;

    public TheVoidClawGroundAtPositionShatterStage(boolean waitForClaw, int relativeHeadX, int relativeHeadY) {
        this.waitForClaw = waitForClaw;
        this.relativeHeadX = relativeHeadX;
        this.relativeHeadY = relativeHeadY;
    }

    public TheVoidClawGroundAtPositionShatterStage(boolean waitForClaw) {
        this(waitForClaw, Integer.MIN_VALUE, Integer.MIN_VALUE);
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
        if (this.relativeHeadX == Integer.MIN_VALUE || this.relativeHeadY == Integer.MIN_VALUE) {
            this.clawMob = ((TheVoidMob)mob).spawnedClaws.stream().filter(TheVoidClawMob::isIdle).findFirst().orElse(null);
            if (this.clawMob != null) {
                this.clawMob.flyToTargetAndSlam(this.clawMob.getX(), this.clawMob.getY(), 500, 500, 2000, 2000, true);
            }
        } else {
            int posX = ((Entity)mob).getX() + this.relativeHeadX;
            int posY = ((Entity)mob).getY() + this.relativeHeadY;
            this.clawMob = ((TheVoidMob)mob).spawnedClaws.stream().filter(TheVoidClawMob::isIdle).min(Comparator.comparingDouble(c -> c.getDistance(posX, posY))).orElse(null);
            if (this.clawMob != null) {
                this.clawMob.flyToTargetAndSlam(posX, posY, 2000, 500, 2000, 2000, true);
            }
        }
        if (!this.waitForClaw) {
            this.clawMob = null;
        }
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

