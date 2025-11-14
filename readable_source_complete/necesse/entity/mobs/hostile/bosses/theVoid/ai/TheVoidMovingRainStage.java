/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.TheVoidMovingRainLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidMovingRainStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public float minAttacksPerSecond;
    public float maxAttacksPerSecond;
    public int minTotalAttacks;
    public int maxTotalAttacks;
    public float keepRunningAndFindNewPositionsAtDistance;
    protected TheVoidMovingRainLevelEvent event;

    public TheVoidMovingRainStage(float minAttacksPerSecond, float maxAttacksPerSecond, int minTotalAttacks, int maxTotalAttacks, float keepRunningAndFindNewPositionsAtDistance) {
        this.minAttacksPerSecond = minAttacksPerSecond;
        this.maxAttacksPerSecond = maxAttacksPerSecond;
        this.minTotalAttacks = minTotalAttacks;
        this.maxTotalAttacks = maxTotalAttacks;
        this.keepRunningAndFindNewPositionsAtDistance = keepRunningAndFindNewPositionsAtDistance;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.keepRunningAndFindNewPositionsAtDistance > 0.0f && this.event != null && !this.event.isOver()) {
            if (!blackboard.mover.isCurrentlyMovingFor(this)) {
                this.findNextPosition(mob, blackboard);
            }
            return AINodeResult.RUNNING;
        }
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        ((TheVoidMob)mob).telegraphVoidRainAbility.runAndSend();
        int totalAttacks = GameMath.lerp(((Mob)mob).getHealthPercent(), this.maxTotalAttacks, this.minTotalAttacks);
        float attacksPerSecond = GameMath.lerp(((Mob)mob).getHealthPercent(), this.maxAttacksPerSecond, this.minAttacksPerSecond);
        this.event = new TheVoidMovingRainLevelEvent((Mob)mob, 75, 400.0f, attacksPerSecond, totalAttacks);
        ((Entity)mob).getLevel().entityManager.events.add(this.event);
        ((TheVoidMob)mob).spawnedEvents.add(this.event);
    }

    protected void findNextPosition(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, "currentTarget");
        if (target == null) {
            return;
        }
        Point2D.Float base = new Point2D.Float(target.x, target.y);
        Point2D.Float pos = new Point2D.Float(((TheVoidMob)mob).x, ((TheVoidMob)mob).y);
        for (int i = 0; i < 10; ++i) {
            int randomAngle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float angleDir = GameMath.getAngleDir(randomAngle);
            pos = new Point2D.Float(base.x + angleDir.x * this.keepRunningAndFindNewPositionsAtDistance, base.y + angleDir.y * this.keepRunningAndFindNewPositionsAtDistance);
        }
        blackboard.mover.directMoveTo(this, (int)pos.x, (int)pos.y);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

