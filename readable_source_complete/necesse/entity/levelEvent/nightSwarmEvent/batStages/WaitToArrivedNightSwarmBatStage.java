/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitToArrivedNightSwarmBatStage
extends NightSwarmBatStage {
    public WaitToArrivedNightSwarmBatStage() {
        super(false);
    }

    @Override
    public void onStarted(NightSwarmBatMob mob) {
    }

    @Override
    public void serverTick(NightSwarmBatMob mob) {
    }

    @Override
    public boolean hasCompleted(NightSwarmBatMob mob) {
        return !mob.hasCurrentMovement() || mob.hasArrivedAtTarget();
    }

    @Override
    public void onCompleted(NightSwarmBatMob mob) {
    }
}

