/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.CommandAttackTargetterAINode;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanCommandAttackTargetterAINode<T extends HumanMob>
extends CommandAttackTargetterAINode<T> {
    @Override
    public Mob getTarget(T mob) {
        return ((HumanMob)mob).commandAttackMob;
    }

    @Override
    public void resetTarget(T mob) {
        ((HumanMob)mob).commandAttackMob = null;
    }

    @Override
    public void tickTargetSet(T mob, Mob target) {
        if (((HumanMob)mob).objectUser != null) {
            ((HumanMob)mob).objectUser.stopUsing();
        }
    }
}

