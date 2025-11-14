/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitToArrivedNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitToArrivedSwarmEventStage
extends NightSwarmEventStage {
    @Override
    public void onStarted(NightSwarmLevelEvent event) {
        for (NightSwarmBatMob bat : event.getBats(false)) {
            bat.stages.add(new WaitToArrivedNightSwarmBatStage());
        }
    }

    @Override
    public void serverTick(NightSwarmLevelEvent event) {
    }

    @Override
    public boolean hasCompleted(NightSwarmLevelEvent event) {
        return true;
    }

    @Override
    public void onCompleted(NightSwarmLevelEvent event) {
    }
}

