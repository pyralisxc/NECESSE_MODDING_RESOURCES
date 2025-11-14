/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovement;

public class MoveNightSwarmBatStage
extends NightSwarmBatStage {
    public MobMovement movement;

    public MoveNightSwarmBatStage(MobMovement movement) {
        super(false);
        this.movement = movement;
    }

    @Override
    public void onStarted(NightSwarmBatMob mob) {
        mob.setMovement(this.movement);
    }

    @Override
    public void serverTick(NightSwarmBatMob mob) {
    }

    @Override
    public boolean hasCompleted(NightSwarmBatMob mob) {
        return this.movement == null || mob.hasArrivedAtTarget() || !mob.hasCurrentMovement();
    }

    @Override
    public void onCompleted(NightSwarmBatMob mob) {
        mob.setMovement(null);
    }
}

