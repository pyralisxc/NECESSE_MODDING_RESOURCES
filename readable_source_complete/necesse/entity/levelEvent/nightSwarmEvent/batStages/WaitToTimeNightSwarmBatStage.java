/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitToTimeNightSwarmBatStage
extends NightSwarmBatStage {
    private long endTime;

    public WaitToTimeNightSwarmBatStage(boolean idleAllowed, long endTime) {
        super(idleAllowed);
        this.endTime = endTime;
    }

    @Override
    public void onStarted(NightSwarmBatMob mob) {
    }

    @Override
    public void serverTick(NightSwarmBatMob mob) {
    }

    @Override
    public boolean hasCompleted(NightSwarmBatMob mob) {
        return this.endTime <= mob.getWorldEntity().getTime();
    }

    @Override
    public void onCompleted(NightSwarmBatMob mob) {
    }
}

