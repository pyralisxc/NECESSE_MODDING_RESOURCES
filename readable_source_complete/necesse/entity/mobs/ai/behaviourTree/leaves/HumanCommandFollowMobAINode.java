/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowMobAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanCommandFollowMobAINode<T extends HumanMob>
extends FollowMobAINode<T> {
    public long nextApplyBuffTime;

    @Override
    public AINodeResult tickFollowing(Mob target, T mob, Blackboard<T> blackboard) {
        AINodeResult out = super.tickFollowing(target, mob, blackboard);
        if (target != null) {
            double distance = GameMath.diagonalMoveDistance(((Entity)mob).getX(), ((Entity)mob).getY(), target.getX(), target.getY());
            if (((HumanMob)mob).commandMoveToFollowPoint && distance <= (double)((float)this.tileRadius * 1.5f * 32.0f)) {
                ((HumanMob)mob).commandMoveToFollowPoint = false;
                blackboard.submitEvent("resetTarget", new AIEvent());
            }
            if (distance > (double)((float)this.tileRadius * 2.5f * 32.0f)) {
                if (this.nextApplyBuffTime <= mob.getTime()) {
                    ((HumanMob)mob).buffManager.addBuff(new ActiveBuff(BuffRegistry.SETTLER_SPRINT, (Mob)mob, 2.0f, null), true);
                    this.nextApplyBuffTime = mob.getTime() + 1500L;
                }
            } else {
                this.nextApplyBuffTime = mob.getTime() + 500L;
            }
        } else {
            this.nextApplyBuffTime = 0L;
            ((HumanMob)mob).commandMoveToFollowPoint = false;
        }
        return out;
    }

    @Override
    public void onMovedToFollowTarget(Mob target, T mob, Blackboard<T> blackboard, boolean foundPosition) {
        super.onMovedToFollowTarget(target, mob, blackboard, foundPosition);
        if (((HumanMob)mob).commandMoveToFollowPoint) {
            ((HumanMob)mob).commandMoveToFollowPoint = false;
            blackboard.submitEvent("resetTarget", new AIEvent());
        }
    }

    @Override
    public Mob getFollowingMob(T mob) {
        if (((HumanMob)mob).isHiding && mob.isSettlerOnCurrentLevel()) {
            return null;
        }
        if (((HumanMob)mob).commandFollowMob != null && ((HumanMob)mob).commandFollowMob.removed()) {
            if (mob.isSettlerWithinSettlement()) {
                ((HumanMob)mob).commandFollowMob = null;
            } else {
                ((HumanMob)mob).commandGuard(null, ((Entity)mob).getX(), ((Entity)mob).getY());
            }
        }
        if (((HumanMob)mob).commandFollowMob != null) {
            if (((HumanMob)mob).objectUser != null) {
                ((HumanMob)mob).objectUser.stopUsing();
            }
            ((HumanMob)mob).setActivity("command", 15000, new LocalMessage("activities", "following", "target", ((HumanMob)mob).commandFollowMob.getLocalization()));
            return ((HumanMob)mob).commandFollowMob;
        }
        return null;
    }
}

