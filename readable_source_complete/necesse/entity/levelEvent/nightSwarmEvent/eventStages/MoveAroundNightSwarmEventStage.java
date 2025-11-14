/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.SetIdleNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class MoveAroundNightSwarmEventStage
extends NightSwarmEventStage {
    public int points;
    public int range;
    public int maxAngle;
    public boolean forceSpread;

    public MoveAroundNightSwarmEventStage(int points, int range, boolean forceSpread, int maxAngle) {
        this.points = points;
        this.range = range;
        this.forceSpread = forceSpread;
        this.maxAngle = GameMath.limit(maxAngle, 0, 360);
    }

    public MoveAroundNightSwarmEventStage(int points, int range, boolean forceSpread) {
        this(points, range, forceSpread, 360);
    }

    @Override
    public void onStarted(NightSwarmLevelEvent event) {
        float angleOffset;
        ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>();
        int anglePerPoint = this.maxAngle / this.points;
        if (this.maxAngle >= 360) {
            angleOffset = GameRandom.globalRandom.nextInt(360);
        } else {
            Point2D.Float dir = GameMath.normalize(event.nextLevelX - event.currentTarget.x, event.nextLevelY - event.currentTarget.y);
            float currentAngle = GameMath.fixAngle(GameMath.getAngle(dir));
            angleOffset = currentAngle - (float)this.maxAngle / 2.0f;
        }
        float averageX = 0.0f;
        float averageY = 0.0f;
        for (int i = 0; i < this.points; ++i) {
            float angle = this.forceSpread ? angleOffset + (float)(i * anglePerPoint) + (float)GameRandom.globalRandom.nextInt(anglePerPoint) : angleOffset + (float)GameRandom.globalRandom.nextInt(this.maxAngle);
            angle = GameMath.fixAngle(angle);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float nextX = event.currentTarget.x + dir.x * (float)this.range;
            float nextY = event.currentTarget.y + dir.y * (float)this.range;
            points.add(new Point2D.Float(nextX, nextY));
            averageX += nextX;
            averageY += nextY;
        }
        for (NightSwarmBatMob bat : event.getBats(false)) {
            Point2D.Float pos = (Point2D.Float)GameRandom.globalRandom.getOneOf(points);
            bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(pos.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), pos.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50))));
            bat.stages.add(new SetIdleNightSwarmBatStage(pos.x, pos.y));
        }
        event.nextLevelX = averageX / (float)points.size();
        event.nextLevelY = averageY / (float)points.size();
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

