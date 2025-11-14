/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.util.Arrays;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedCageStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedFlyToCenterStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedPushStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;

public class AscendedCageRotationStage<T extends AscendedWizardMob>
extends AttackStageManagerNode<T> {
    protected int nextHealthPercentTriggerIndex;
    protected float[] healthPercentTriggers;
    protected boolean isRunning;

    public AscendedCageRotationStage(float ... healthPercentTriggers) {
        this.healthPercentTriggers = healthPercentTriggers;
        Arrays.sort(healthPercentTriggers);
        this.nextHealthPercentTriggerIndex = healthPercentTriggers.length - 1;
        this.addChild(new AscendedFlyToCenterStage(true));
        this.addChild(new AscendedPushStage());
        this.addChild(new IdleTimeAttackStage(400));
        this.addChild(new AscendedCageStage());
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (!this.isRunning) {
            return AINodeResult.SUCCESS;
        }
        return super.tick(mob, blackboard);
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.isRunning = false;
        while (this.nextHealthPercentTriggerIndex > 0 && ((Mob)mob).getHealthPercent() < this.healthPercentTriggers[this.nextHealthPercentTriggerIndex]) {
            --this.nextHealthPercentTriggerIndex;
            this.isRunning = true;
        }
        if (this.isRunning) {
            super.onStarted(mob, blackboard);
        }
    }
}

