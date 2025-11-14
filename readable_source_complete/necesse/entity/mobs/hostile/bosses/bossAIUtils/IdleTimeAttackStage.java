/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.util.function.Function;
import necesse.engine.util.tween.EaseFunction;
import necesse.engine.util.tween.Easings;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class IdleTimeAttackStage<T extends Mob>
extends AINode<T>
implements AttackStageInterface<T> {
    public Function<T, Integer> idleTimeMSGetter;
    public int timer;

    public IdleTimeAttackStage(Function<T, Integer> idleTimeMSGetter) {
        this.idleTimeMSGetter = idleTimeMSGetter;
    }

    public IdleTimeAttackStage(int idleTimeMS) {
        this(m -> idleTimeMS);
    }

    public IdleTimeAttackStage(int noHealthIdleTime, int fullHealthIdleTime, EaseFunction easing) {
        this(m -> {
            int delta = fullHealthIdleTime - noHealthIdleTime;
            float healthPerc = m.getHealthPercent();
            float progress = easing.ease(healthPerc);
            return noHealthIdleTime + (int)((float)delta * progress);
        });
    }

    public IdleTimeAttackStage(int noHealthIdleTime, int fullHealthIdleTime) {
        this(noHealthIdleTime, fullHealthIdleTime, Easings.Linear);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.timer = 0;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        this.timer += 50;
        if (this.timer >= this.idleTimeMSGetter.apply(mob)) {
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.RUNNING;
    }
}

