/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class SetIdleNightSwarmBatStage
extends NightSwarmBatStage {
    public float idleXPos;
    public float idleYPos;

    public SetIdleNightSwarmBatStage(float idleXPos, float idleYPos) {
        super(true);
        this.idleXPos = idleXPos;
        this.idleYPos = idleYPos;
    }

    @Override
    public void onStarted(NightSwarmBatMob mob) {
        mob.idleXPos = this.idleXPos;
        mob.idleYPos = this.idleYPos;
    }

    @Override
    public void serverTick(NightSwarmBatMob mob) {
    }

    @Override
    public boolean hasCompleted(NightSwarmBatMob mob) {
        return true;
    }

    @Override
    public void onCompleted(NightSwarmBatMob mob) {
    }
}

