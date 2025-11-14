/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class AngleSplitNightSwarmBatStage
extends NightSwarmBatStage {
    public float midX;
    public float midY;
    public int angleSegments;
    public float angleOffset;
    public int range;

    public AngleSplitNightSwarmBatStage(float midX, float midY, int angleSegments, float angleOffset, int range) {
        super(false);
        this.midX = midX;
        this.midY = midY;
        this.angleSegments = angleSegments;
        this.angleOffset = angleOffset;
        this.range = range;
    }

    @Override
    public void onStarted(NightSwarmBatMob mob) {
        float anglePerSegment = 360.0f / (float)this.angleSegments;
        Point2D.Float currentDir = GameMath.normalize(mob.x - this.midX, mob.y - this.midY);
        float currentAngle = GameMath.getAngle(currentDir);
        currentAngle = GameMath.fixAngle(currentAngle + anglePerSegment / 2.0f + this.angleOffset);
        int angleIndex = (int)(currentAngle / anglePerSegment);
        float targetAngle = GameMath.fixAngle((float)angleIndex * anglePerSegment - this.angleOffset);
        Point2D.Float targetDir = GameMath.getAngleDir(targetAngle);
        mob.setMovement(new MobMovementLevelPos(this.midX + targetDir.x * (float)this.range, this.midY + targetDir.y * (float)this.range));
        mob.idleXPos = this.midX + targetDir.x * (float)this.range;
        mob.idleYPos = this.midY + targetDir.y * (float)this.range;
    }

    @Override
    public void serverTick(NightSwarmBatMob mob) {
    }

    @Override
    public boolean hasCompleted(NightSwarmBatMob mob) {
        return mob.hasArrivedAtTarget() || !mob.hasCurrentMovement();
    }

    @Override
    public void onCompleted(NightSwarmBatMob mob) {
        mob.setMovement(null);
    }
}

