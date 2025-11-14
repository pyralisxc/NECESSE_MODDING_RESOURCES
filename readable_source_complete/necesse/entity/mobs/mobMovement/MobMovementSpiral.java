/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovement;

public abstract class MobMovementSpiral
extends MobMovement {
    public long startTime;
    public float outerRadius;
    public int semiCircles;
    public float radiusDecreasePerSemiCircle;
    public float speed;
    public float startAngle;
    public boolean clockwise;
    public int currentSemiCircle;
    public long currentTimePassed;

    public static int getSemiCircles(float outerRadius, float radiusDecreasePerSemiCircle, float minInnerRadius) {
        float totalRadiusDecrease = outerRadius - minInnerRadius;
        return (int)(totalRadiusDecrease / radiusDecreasePerSemiCircle);
    }

    public MobMovementSpiral() {
    }

    public MobMovementSpiral(Mob mob, float outerRadius, int semiCircles, float radiusDecreasePerSemiCircle, float speed, float startAngle, boolean clockwise) {
        this();
        this.startTime = mob.getWorldEntity().getTime();
        this.outerRadius = outerRadius;
        this.semiCircles = semiCircles;
        this.radiusDecreasePerSemiCircle = radiusDecreasePerSemiCircle;
        this.speed = speed;
        this.startAngle = startAngle;
        this.clockwise = clockwise;
    }

    public MobMovementSpiral(Mob mob, float centerX, float centerY, float outerRadius, int semiCircles, float radiusDecreasePerSemiCircle, float speed, boolean clockwise) {
        this(mob, outerRadius, semiCircles, radiusDecreasePerSemiCircle, speed, 0.0f, clockwise);
        Point2D.Float dir = GameMath.normalize(centerX - mob.x, centerY - mob.y);
        this.startAngle = GameMath.fixAngle(GameMath.getAngle(dir) - 90.0f);
    }

    @Override
    public void setupPacket(Mob mob, PacketWriter writer) {
        writer.putNextLong(this.startTime);
        writer.putNextFloat(this.outerRadius);
        writer.putNextInt(this.semiCircles);
        writer.putNextFloat(this.radiusDecreasePerSemiCircle);
        writer.putNextFloat(this.speed);
        writer.putNextFloat(this.startAngle);
        writer.putNextBoolean(this.clockwise);
    }

    @Override
    public void applyPacket(Mob mob, PacketReader reader) {
        this.startTime = reader.getNextLong();
        this.outerRadius = reader.getNextFloat();
        this.semiCircles = reader.getNextInt();
        this.radiusDecreasePerSemiCircle = reader.getNextFloat();
        this.speed = reader.getNextFloat();
        this.startAngle = reader.getNextFloat();
        this.clockwise = reader.getNextBoolean();
        this.tick(mob);
    }

    public abstract Point2D.Float getCenterPos();

    public float getCurrentRadius() {
        return Math.abs(this.outerRadius - (float)this.currentSemiCircle * this.radiusDecreasePerSemiCircle);
    }

    public Point2D.Float getCurrentPos(Mob mob) {
        Point2D.Float centerPos = this.getCenterPos();
        if (centerPos != null) {
            long timeAlongCurrentSemiCircle;
            int timeForCurrentSemiCircle;
            float currentRadius;
            while (true) {
                currentRadius = this.getCurrentRadius();
                timeForCurrentSemiCircle = this.getTimeForSemiCircle(currentRadius);
                long timeSinceStart = mob.getWorldEntity().getTime() - this.startTime;
                timeAlongCurrentSemiCircle = timeSinceStart - this.currentTimePassed;
                if (timeAlongCurrentSemiCircle <= (long)timeForCurrentSemiCircle) break;
                this.currentTimePassed += (long)timeForCurrentSemiCircle;
                ++this.currentSemiCircle;
            }
            float percentAlongSemiCircle = (float)timeAlongCurrentSemiCircle / (float)timeForCurrentSemiCircle;
            float currentAngle = percentAlongSemiCircle * 180.0f;
            if (this.currentSemiCircle % 2 != 0) {
                currentAngle += 180.0f;
            }
            if (!this.clockwise) {
                currentAngle = -currentAngle;
            }
            currentAngle = this.startAngle + currentAngle;
            double cos = GameMath.cos(currentAngle);
            double sin = GameMath.sin(currentAngle);
            return new Point2D.Float(centerPos.x + (float)(sin * (double)currentRadius), centerPos.y + (float)(-(cos * (double)currentRadius)));
        }
        return null;
    }

    @Override
    public boolean tick(Mob mob) {
        Point2D.Float pos = this.getCurrentPos(mob);
        if (pos != null) {
            this.moveTo(mob, pos.x, pos.y, mob.moveAccuracy);
        }
        return this.currentSemiCircle > this.semiCircles;
    }

    public static int getTimeForSemiCircle(float radius, float speed) {
        float circumference = (float)((double)radius * Math.PI);
        float distanceMovedPerSecond = 1000.0f * speed / 250.0f;
        float secondsToMoveCircumference = circumference / distanceMovedPerSecond;
        return (int)(secondsToMoveCircumference * 1000.0f);
    }

    public int getTimeForSemiCircle(float radius) {
        return MobMovementSpiral.getTimeForSemiCircle(radius, this.speed);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobMovementSpiral) {
            MobMovementSpiral other = (MobMovementSpiral)obj;
            return this.startTime == other.startTime && this.outerRadius == other.outerRadius && this.semiCircles == other.semiCircles && this.radiusDecreasePerSemiCircle == other.radiusDecreasePerSemiCircle && this.speed == other.speed && this.startAngle == other.startAngle && this.clockwise == other.clockwise;
        }
        return false;
    }
}

