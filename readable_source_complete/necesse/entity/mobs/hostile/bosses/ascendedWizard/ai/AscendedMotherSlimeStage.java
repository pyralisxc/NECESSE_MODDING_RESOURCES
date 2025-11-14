/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedSlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedSlimeQuakeWarningEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class AscendedMotherSlimeStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public long warningTime;
    private int slimeOffset;
    private float slimeVelocity;
    public long quakeTime;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.warningTime != 0L && this.warningTime <= mob.getTime()) {
            int timeToQuake = (int)(this.quakeTime - mob.getTime());
            AscendedSlimeQuakeWarningEvent event = new AscendedSlimeQuakeWarningEvent((Mob)mob, ((Entity)mob).getX(), ((Entity)mob).getY(), new GameRandom(), 0.0f, 360.0f, this.slimeVelocity, 1200.0f, timeToQuake, (float)this.slimeOffset);
            ((Entity)mob).getLevel().entityManager.events.add(event);
            ((AscendedWizardMob)mob).spawnedEvents.add(event);
            this.warningTime = 0L;
            return AINodeResult.RUNNING;
        }
        if (this.quakeTime != 0L && this.quakeTime <= mob.getTime()) {
            AscendedSlimeQuakeEvent event = new AscendedSlimeQuakeEvent((Mob)mob, ((Entity)mob).getX(), ((Entity)mob).getY(), new GameRandom(), 0.0f, 360.0f, AscendedWizardMob.slimeCircleDamage, this.slimeVelocity, 50.0f, 1200.0f, this.slimeOffset);
            ((Entity)mob).getLevel().entityManager.events.add(event);
            ((AscendedWizardMob)mob).spawnedEvents.add(event);
            this.quakeTime = 0L;
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        float exp = GameMath.expSmooth(((Mob)mob).getHealthPercent(), 1.0f, 0.3f);
        this.warningTime = mob.getTime() + 750L;
        this.quakeTime = this.warningTime + (long)GameMath.lerp(exp, 750, 1500);
        this.slimeOffset = GameRandom.globalRandom.nextInt(15000);
        this.slimeVelocity = GameMath.lerp(exp, 1000, 250);
        ((AscendedWizardMob)mob).playBossSoundAbility.runAndSend(AscendedWizardMob.BossSound.MOTHER_SLIME);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

