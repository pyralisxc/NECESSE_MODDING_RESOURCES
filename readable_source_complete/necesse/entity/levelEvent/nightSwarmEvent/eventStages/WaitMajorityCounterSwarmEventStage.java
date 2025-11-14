/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.CounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitMajorityCounterSwarmEventStage
extends NightSwarmEventStage {
    public NightSwarmCompletedCounter counter;

    @Override
    public void onStarted(NightSwarmLevelEvent event) {
        this.counter = new NightSwarmCompletedCounter();
        for (NightSwarmBatMob bat : event.getBats(false)) {
            bat.stages.add(new CounterNightSwarmBatStage(true, this.counter));
        }
    }

    @Override
    public void serverTick(NightSwarmLevelEvent event) {
    }

    @Override
    public boolean hasCompleted(NightSwarmLevelEvent event) {
        return this.counter == null || this.counter.isMajorityComplete();
    }

    @Override
    public void onCompleted(NightSwarmLevelEvent event) {
    }
}

