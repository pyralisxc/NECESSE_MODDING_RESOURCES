/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitCounterNightSwarmBatStage
extends NightSwarmBatStage {
    public NightSwarmCompletedCounter counter;

    public WaitCounterNightSwarmBatStage(boolean idleAllowed, NightSwarmCompletedCounter counter) {
        super(idleAllowed);
        this.counter = counter;
    }

    @Override
    public void onStarted(NightSwarmBatMob mob) {
    }

    @Override
    public void serverTick(NightSwarmBatMob mob) {
    }

    @Override
    public boolean hasCompleted(NightSwarmBatMob mob) {
        return this.counter.isComplete();
    }

    @Override
    public void onCompleted(NightSwarmBatMob mob) {
    }
}

