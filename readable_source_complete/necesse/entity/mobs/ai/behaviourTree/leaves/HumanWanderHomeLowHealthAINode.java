/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.leaves.WanderHomeAtConditionAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanWanderHomeLowHealthAINode<T extends HumanMob>
extends WanderHomeAtConditionAINode<T> {
    public long nextApplyBuffTime;

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            if (this.nextApplyBuffTime <= mob.getTime()) {
                ((HumanMob)mob).buffManager.addBuff(new ActiveBuff(BuffRegistry.SETTLER_SPRINT, (Mob)mob, 2.0f, null), true);
                this.nextApplyBuffTime = mob.getTime() + 1500L;
            }
        } else {
            this.nextApplyBuffTime = 0L;
        }
        return super.tickNode(mob, blackboard);
    }

    @Override
    public boolean shouldGoHome(T mob) {
        if (((HumanMob)mob).isVisitor() || !mob.isSettlerOnCurrentLevel()) {
            return false;
        }
        return ((HumanMob)mob).isHiding;
    }

    @Override
    public Point getHomeTile(T mob) {
        if (!mob.isSettlerOnCurrentLevel()) {
            return null;
        }
        return ((HumanMob)mob).home;
    }

    @Override
    public boolean isHomeRoom(T mob) {
        return mob.isSettlerOnCurrentLevel();
    }

    @Override
    public boolean isHomeHouse(T mob) {
        return true;
    }
}

