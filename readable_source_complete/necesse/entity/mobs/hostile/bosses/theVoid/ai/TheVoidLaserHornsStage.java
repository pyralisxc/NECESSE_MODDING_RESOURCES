/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheVoidHornBeamLevelEvent;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidHornMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidLaserHornsStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    private final ArrayList<TheVoidHornBeamLevelEvent> events = new ArrayList();

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.events.isEmpty() || this.events.stream().allMatch(LevelEvent::isOver)) {
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.events.clear();
        for (LevelMob<TheVoidHornMob> spawnedHorn : ((TheVoidMob)mob).spawnedHorns) {
            TheVoidHornMob horn = spawnedHorn.get(((Entity)mob).getLevel());
            if (horn == null) continue;
            TheVoidHornBeamLevelEvent event = new TheVoidHornBeamLevelEvent(horn, horn.isLeftHorn, 90.0f + (horn.isLeftHorn ? 30.0f : -30.0f), 90.0f + (horn.isLeftHorn ? -25.0f : 25.0f), mob.getTime(), 1700, GameRandom.globalRandom.nextInt(), 1200.0f, TheVoidMob.laserDamage, 100, 1000, 0);
            this.events.add(event);
            ((Entity)mob).getLevel().entityManager.events.add(event);
            ((TheVoidMob)mob).spawnedEvents.add(event);
        }
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

