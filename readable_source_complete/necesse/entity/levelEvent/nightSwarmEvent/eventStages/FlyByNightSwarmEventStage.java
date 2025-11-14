/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.SetIdleNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitFromNowNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class FlyByNightSwarmEventStage
extends NightSwarmEventStage {
    public int startOffset;
    public int endOffset;
    public int startMaxRandomOffset;
    public int endMaxRandomOffset;
    public int startEndDistance;
    public int endEndDistance;

    public FlyByNightSwarmEventStage(int startOffset, int endOffset, int startMaxRandomOffset, int endMaxRandomOffset, int startEndDistance, int endEndDistance) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.startMaxRandomOffset = startMaxRandomOffset;
        this.endMaxRandomOffset = endMaxRandomOffset;
        this.startEndDistance = startEndDistance;
        this.endEndDistance = endEndDistance;
    }

    public FlyByNightSwarmEventStage(int offset, int maxRandomOffset, int endDistance) {
        this(offset, offset, maxRandomOffset, maxRandomOffset, endDistance, endDistance);
    }

    @Override
    public void onStarted(NightSwarmLevelEvent event) {
        Point2D.Float dir = GameMath.normalize(event.currentTarget.x - event.nextLevelX, event.currentTarget.y - event.nextLevelY);
        float angle = GameMath.getAngle(dir);
        float endAngle = angle + (float)GameRandom.globalRandom.getIntBetween(-20, 20);
        dir = GameMath.getAngleDir(endAngle);
        int offset = GameMath.lerp(event.lastHealthProgress, this.startOffset, this.endOffset);
        int maxRandomOffset = GameMath.lerp(event.lastHealthProgress, this.startMaxRandomOffset, this.endMaxRandomOffset);
        int endDistance = GameMath.lerp(event.lastHealthProgress, this.startEndDistance, this.endEndDistance);
        int randomOffset = GameRandom.globalRandom.getIntBetween(-maxRandomOffset, maxRandomOffset);
        Point2D.Float point1 = GameMath.getPerpendicularPoint(event.currentTarget.x, event.currentTarget.y, (float)(offset + randomOffset), dir);
        Point2D.Float point2 = GameMath.getPerpendicularPoint(event.currentTarget.x, event.currentTarget.y, (float)(-offset + randomOffset), dir);
        Point2D.Float endPoint = new Point2D.Float(event.currentTarget.x + dir.x * (float)endDistance, event.currentTarget.y + dir.y * (float)endDistance);
        int index = 0;
        for (NightSwarmBatMob bat : event.getBats(false)) {
            int randomX = GameRandom.globalRandom.getIntBetween(-60, 60);
            int randomY = GameRandom.globalRandom.getIntBetween(-60, 60);
            bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(event.nextLevelX + (float)randomX, event.nextLevelY + (float)randomY)));
            bat.stages.add(new WaitFromNowNightSwarmBatStage(true, bat, index * 25));
            Point2D.Float p = GameRandom.globalRandom.getOneOf(point1, point2);
            bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(p.x + (float)randomX, p.y + (float)randomY)));
            bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(endPoint.x + (float)randomX, endPoint.y + (float)randomY)));
            bat.stages.add(new SetIdleNightSwarmBatStage(endPoint.x, endPoint.y));
            ++index;
        }
        event.nextLevelX = endPoint.x;
        event.nextLevelY = endPoint.y;
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

