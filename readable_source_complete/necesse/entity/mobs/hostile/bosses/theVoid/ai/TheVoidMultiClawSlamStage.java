/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.awt.geom.Point2D;
import java.util.Comparator;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidMultiClawSlamStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public float randomMoveRangeAroundTarget;
    public int totalTime;
    public float slamsPerSecAtNoHealth;
    public float slamsPerSecAtFullHealth;
    protected long startTime;
    protected float nextSlamBuffer;

    public TheVoidMultiClawSlamStage(float randomMoveRangeAroundTarget, int totalTime, float slamsPerSecAtNoHealth, float slamsPerSecAtFullHealth) {
        this.randomMoveRangeAroundTarget = randomMoveRangeAroundTarget;
        this.totalTime = totalTime;
        this.slamsPerSecAtNoHealth = slamsPerSecAtNoHealth;
        this.slamsPerSecAtFullHealth = slamsPerSecAtFullHealth;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        long timeSinceStart = mob.getTime() - this.startTime;
        if (timeSinceStart >= (long)this.totalTime) {
            return AINodeResult.SUCCESS;
        }
        if (!blackboard.mover.isCurrentlyMovingFor(this)) {
            this.findNextPosition(mob, blackboard);
        }
        float slamsPerSec = GameMath.lerp(((Mob)mob).getHealthPercent(), this.slamsPerSecAtNoHealth, this.slamsPerSecAtFullHealth);
        float slamsPerTick = slamsPerSec / 20.0f;
        this.nextSlamBuffer += slamsPerTick;
        while (this.nextSlamBuffer >= 1.0f) {
            this.nextSlamBuffer -= 1.0f;
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target == null) {
                this.nextSlamBuffer = 0.0f;
                break;
            }
            TheVoidClawMob claw = ((TheVoidMob)mob).spawnedClaws.stream().filter(TheVoidClawMob::isIdle).min(Comparator.comparingDouble(c -> c.getDistance(target))).orElse(null);
            if (claw == null) continue;
            float offsetX = GameMath.limit(target.dx * 10.0f, -50.0f, 50.0f);
            float offsetY = GameMath.limit(target.dy * 10.0f, -50.0f, 50.0f);
            claw.flyToTargetAndSlam(target, offsetX, offsetY, 2000, 300, 250, 1000, false);
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.startTime = mob.getTime();
        this.nextSlamBuffer = 0.0f;
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
            pos = new Point2D.Float(base.x + angleDir.x * this.randomMoveRangeAroundTarget, base.y + angleDir.y * this.randomMoveRangeAroundTarget);
        }
        blackboard.mover.directMoveTo(this, (int)pos.x, (int)pos.y);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

