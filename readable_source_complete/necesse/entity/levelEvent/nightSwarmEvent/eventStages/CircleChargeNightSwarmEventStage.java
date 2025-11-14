/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.CircleEndNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitCounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class CircleChargeNightSwarmEventStage
extends NightSwarmEventStage {
    public float midX;
    public float midY;
    public int circleRange;
    public boolean forceComplete;
    public NightSwarmCompletedCounter completedCounter = new NightSwarmCompletedCounter();

    @Override
    public void onStarted(NightSwarmLevelEvent event) {
        this.midX = event.currentTarget.x;
        this.midY = event.currentTarget.y;
        this.forceComplete = false;
        long currentTime = event.level.getWorldEntity().getTime();
        int timeBeforeStartCharge = GameMath.lerp(event.lastHealthProgress, 6000, 2000);
        long timeBetweenCharges = GameMath.lerp(event.lastHealthProgress, 800, 200);
        long endTime = currentTime + (long)GameMath.lerp(event.lastHealthProgress, 12000, 6000);
        this.circleRange = GameMath.lerp(event.lastHealthProgress, 450, 300);
        this.completedCounter = new NightSwarmCompletedCounter();
        int index = 0;
        int chargeJumpPerIndex = GameRandom.prime(GameRandom.globalRandom.getIntBetween(event.bats.size(), event.bats.size() + 10));
        for (NightSwarmBatMob bat : event.getBats(true)) {
            if (bat != null && !bat.removed()) {
                float midX = this.midX + GameRandom.globalRandom.getFloatBetween(-50.0f, 50.0f);
                float midY = this.midY + GameRandom.globalRandom.getFloatBetween(-50.0f, 50.0f);
                int chargeIndex = index * chargeJumpPerIndex % event.bats.size();
                bat.stages.add(new WaitNightSwarmBatStage(true, GameRandom.globalRandom.getIntBetween(0, (int)((float)timeBeforeStartCharge / 1.5f))));
                bat.stages.add(new CircleEndNightSwarmBatStage(midX, midY, midX, midY, this.circleRange, currentTime + (long)timeBeforeStartCharge + (long)chargeIndex * timeBetweenCharges, endTime));
                bat.stages.add(new WaitNightSwarmBatStage(false, GameRandom.globalRandom.getIntBetween(0, 1000)).addCompletedCounter(this.completedCounter));
                bat.stages.add(new WaitCounterNightSwarmBatStage(false, this.completedCounter));
            }
            ++index;
        }
        event.nextLevelX = event.currentTarget.x;
        event.nextLevelY = event.currentTarget.y;
    }

    @Override
    public void serverTick(NightSwarmLevelEvent event) {
        boolean targetFound = event.level.entityManager.players.streamAreaTileRange((int)this.midX, (int)this.midY, (this.circleRange + 50) / 32).filter(m -> m != null && !m.removed() && m.isVisible()).anyMatch(m -> m.getDistance(this.midX, this.midY) <= (float)this.circleRange);
        if (!targetFound) {
            this.forceComplete = true;
        }
    }

    @Override
    public boolean hasCompleted(NightSwarmLevelEvent event) {
        return this.completedCounter.isComplete() || this.forceComplete;
    }

    @Override
    public void onCompleted(NightSwarmLevelEvent event) {
        for (NightSwarmBatMob bat : event.getBats(false)) {
            bat.clearStages();
        }
    }
}

