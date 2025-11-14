/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import java.util.Comparator;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.CounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.SetIdleNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitToTimeNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class ChargeNightSwarmEventStage
extends NightSwarmEventStage {
    public int endDistance;
    public NightSwarmCompletedCounter counter;

    public ChargeNightSwarmEventStage(int endDistance) {
        this.endDistance = endDistance;
    }

    @Override
    public void onStarted(NightSwarmLevelEvent event) {
        double averageX = 0.0;
        double averageY = 0.0;
        int averageCount = 0;
        long currentTime = event.level.getWorldEntity().getTime();
        this.counter = new NightSwarmCompletedCounter();
        for (NightSwarmBatMob bat : event.getBats(false)) {
            PlayerMob target = event.level.entityManager.players.streamAreaTileRange(bat.getX(), bat.getY(), 31).filter(m -> m != null && !m.removed() && m.isVisible()).findBestDistance(0, Comparator.comparingDouble(bat::getDistance)).orElse(null);
            bat.stages.add(new WaitToTimeNightSwarmBatStage(true, currentTime + (long)GameRandom.globalRandom.nextInt(500)));
            if (target != null) {
                Point2D.Float dir = GameMath.normalize(target.x - bat.x, target.y - bat.y);
                float endX = target.x + dir.x * (float)this.endDistance;
                float endY = target.y + dir.y * (float)this.endDistance;
                bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(endX + (float)GameRandom.globalRandom.getIntBetween(-50, 50), endY + (float)GameRandom.globalRandom.getIntBetween(-50, 50))));
                bat.stages.add(new SetIdleNightSwarmBatStage(endX, endY));
                averageX += (double)endX;
                averageY += (double)endY;
            } else {
                averageX += (double)bat.x;
                averageY += (double)bat.y;
            }
            bat.stages.add(new CounterNightSwarmBatStage(true, this.counter));
            ++averageCount;
        }
        event.nextLevelX = (float)(averageX / (double)averageCount);
        event.nextLevelY = (float)(averageY / (double)averageCount);
    }

    @Override
    public void serverTick(NightSwarmLevelEvent event) {
    }

    @Override
    public boolean hasCompleted(NightSwarmLevelEvent event) {
        return this.counter.isMajorityComplete();
    }

    @Override
    public void onCompleted(NightSwarmLevelEvent event) {
    }
}

