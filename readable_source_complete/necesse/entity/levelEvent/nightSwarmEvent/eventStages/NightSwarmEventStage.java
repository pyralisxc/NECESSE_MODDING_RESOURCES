/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;

public abstract class NightSwarmEventStage {
    public abstract void onStarted(NightSwarmLevelEvent var1);

    public abstract void serverTick(NightSwarmLevelEvent var1);

    public abstract boolean hasCompleted(NightSwarmLevelEvent var1);

    public abstract void onCompleted(NightSwarmLevelEvent var1);

    public boolean majorityOfBatsCompleted(NightSwarmLevelEvent event) {
        return event.batsDoneWithStages >= Math.min((int)((float)Math.max(event.bats.size(), 10) / 1.5f), event.bats.size());
    }
}

