/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidConjureMoreClawsStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    private final int fadeDuration = 500;
    public boolean completedFadeIn;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.completedFadeIn) {
            return ((TheVoidMob)mob).getFadeAlpha() >= 1.0f ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
        }
        long timeSinceFadeStart = ((TheVoidMob)mob).getTimeSinceFadeStart();
        if (timeSinceFadeStart >= 1000L) {
            ((TheVoidMob)mob).startFadeAbility.runAndSendFadeOut(500);
            while (((TheVoidMob)mob).spawnedClaws.size() < 4) {
                ((TheVoidMob)mob).spawnClaw();
            }
            for (TheVoidClawMob spawnedClaw : ((TheVoidMob)mob).spawnedClaws) {
                spawnedClaw.teleportToMasterIfIdle();
            }
            this.completedFadeIn = true;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        if (((TheVoidMob)mob).spawnedClaws.size() < 4) {
            ((TheVoidMob)mob).startFadeAbility.runAndSendFadeIn(500, 1000);
            this.completedFadeIn = false;
        } else {
            this.completedFadeIn = true;
        }
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

