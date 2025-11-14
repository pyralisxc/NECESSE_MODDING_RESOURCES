/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToOppositeDirectionAttackStage;

public class AscendedChargeTargetStage<T extends AscendedWizardMob>
extends FlyToOppositeDirectionAttackStage<T> {
    private final boolean addBuff;
    private final boolean removeBuff;

    public AscendedChargeTargetStage(boolean addBuff, boolean removeBuff) {
        super(true, 250.0f, 0.0f);
        this.addBuff = addBuff;
        this.removeBuff = removeBuff;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        super.onStarted(mob, blackboard);
        if (this.addBuff) {
            ((AscendedWizardMob)mob).buffManager.addBuff(new ActiveBuff(BuffRegistry.ASCENDED_DASH, (Mob)mob, 10.0f, null), true);
        }
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
        super.onEnded(mob, blackboard);
        if (this.removeBuff) {
            ((AscendedWizardMob)mob).buffManager.removeBuff(BuffRegistry.ASCENDED_DASH, true);
        }
    }
}

