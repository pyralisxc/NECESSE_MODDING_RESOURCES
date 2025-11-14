/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheVoidBreathBeamLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidLaserMouthStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    private TheVoidBreathBeamLevelEvent event;
    private final int endAtTimeLeft;

    public TheVoidLaserMouthStage(int endAtTimeLeft) {
        this.endAtTimeLeft = endAtTimeLeft;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.event == null || this.event.isOver() || this.event.getTimeLeft() < this.endAtTimeLeft) {
            this.event = null;
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.event = new TheVoidBreathBeamLevelEvent((Mob)mob, 90.0f, 90.0f, mob.getTime(), 2000, GameRandom.globalRandom.nextInt(), 1500.0f, TheVoidMob.breathAttackDamage, 100, 1000, 0);
        ((Entity)mob).getLevel().entityManager.events.add(this.event);
        ((TheVoidMob)mob).spawnedEvents.add(this.event);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

