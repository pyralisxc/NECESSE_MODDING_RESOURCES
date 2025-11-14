/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class AscendedOrbRingStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, "currentTarget");
        if (target != null) {
            ((AscendedWizardMob)mob).spawnOrbRingAbility.runAndSend(target, ((AscendedWizardMob)mob).isTransformed() ? 24 : 8, 5000, 2000, GameRandom.globalRandom.nextInt());
        }
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

