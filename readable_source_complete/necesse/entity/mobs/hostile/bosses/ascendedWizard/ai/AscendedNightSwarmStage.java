/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import necesse.entity.Entity;
import necesse.entity.levelEvent.AscendedBatJailLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class AscendedNightSwarmStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public AscendedBatJailLevelEvent event;
    public long channelEndTime;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (mob.getTime() < this.channelEndTime) {
            return AINodeResult.RUNNING;
        }
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Point baseTile = ((AscendedWizardMob)mob).getBaseTile(blackboard);
        int centerX = baseTile.x * 32 + 16;
        int centerY = baseTile.y * 32 + 16;
        int startDistance = 800;
        int edges = ((AscendedWizardMob)mob).isTransformed() ? 6 : 4;
        int edgeLength = (int)((float)startDistance / ((float)edges / 8.0f) * 2.0f);
        this.event = new AscendedBatJailLevelEvent((Mob)mob, centerX, centerY, edges, 175, startDistance, edgeLength, 3000);
        ((Entity)mob).getLevel().entityManager.events.addHidden(this.event);
        ((AscendedWizardMob)mob).spawnedEvents.add(this.event);
        ((AscendedWizardMob)mob).playBossSoundAbility.runAndSend(AscendedWizardMob.BossSound.NIGHT_SWARM);
        int channelTime = ((AscendedWizardMob)mob).isTransformed() ? 4000 : 6000;
        ((AscendedWizardMob)mob).startChannelAnimation.runAndSend(channelTime);
        this.channelEndTime = mob.getTime() + (long)channelTime;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

