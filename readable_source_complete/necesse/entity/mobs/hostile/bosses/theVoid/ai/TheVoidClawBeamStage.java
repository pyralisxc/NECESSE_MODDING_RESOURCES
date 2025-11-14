/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidClawBeamStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public boolean waitForClaws;
    protected List<TheVoidClawMob> claws;

    public TheVoidClawBeamStage(boolean waitForClaws) {
        this.waitForClaws = waitForClaws;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.claws == null || this.claws.isEmpty() || this.claws.stream().allMatch(TheVoidClawMob::isIdle)) {
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
        List claws = ((TheVoidMob)mob).spawnedClaws.stream().filter(TheVoidClawMob::isIdle).collect(Collectors.toList());
        boolean isAnyHornBroken = ((TheVoidMob)mob).isAnyHornBroken();
        boolean reversed = GameRandom.globalRandom.nextBoolean();
        for (TheVoidClawMob claw : claws) {
            claw.startBeamAbility.runAndSend(GameRandom.globalRandom.nextInt(), 30.0f, 55 + (isAnyHornBroken ? 15 : 0), isAnyHornBroken ? 1800 : 2500, reversed);
        }
        this.claws = this.waitForClaws ? claws : null;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

