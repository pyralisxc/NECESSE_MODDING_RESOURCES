/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import necesse.entity.Entity;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidHornMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidReformHornsStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public int fadeDuration = 500;
    public boolean alwaysReform;
    protected boolean completedFadeIn;

    public TheVoidReformHornsStage(boolean alwaysReform) {
        this.alwaysReform = alwaysReform;
    }

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
        if (timeSinceFadeStart >= (long)(this.fadeDuration + 500)) {
            ((TheVoidMob)mob).startFadeAbility.runAndSendFadeOut(this.fadeDuration);
            ((TheVoidMob)mob).setVulnerableAbility.runAndSend(false);
            for (LevelMob<TheVoidHornMob> lm : ((TheVoidMob)mob).spawnedHorns) {
                TheVoidHornMob horn = lm.get(((Entity)mob).getLevel());
                if (horn == null) continue;
                horn.setBrokenAbility.runAndSend(false);
            }
            ((TheVoidMob)mob).regenHornsAtPercentHealth = 0.0f;
            this.completedFadeIn = true;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        if (!((TheVoidMob)mob).isAnyHornBroken() || !this.alwaysReform && ((Mob)mob).getHealthPercent() > ((TheVoidMob)mob).regenHornsAtPercentHealth) {
            this.completedFadeIn = true;
            return;
        }
        ((TheVoidMob)mob).startFadeAbility.runAndSendFadeIn(this.fadeDuration, 1000);
        ((TheVoidMob)mob).regenHornsAtPercentHealth = 0.0f;
        this.completedFadeIn = false;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

