/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovement;

public abstract class MobMovementCircle
extends MobMovement {
    public int range;
    public float speed;
    public float angleOffset;
    public boolean reversed;

    public MobMovementCircle() {
    }

    public MobMovementCircle(Mob mob, int range, float speed, float angleOffset, boolean reversed) {
        this();
        this.range = range;
        this.speed = speed;
        this.angleOffset = angleOffset;
        this.reversed = reversed;
    }

    public MobMovementCircle(Mob mob, float centerX, float centerY, int range, float speed, boolean reversed) {
        this(mob, range, speed, 0.0f, reversed);
        float startAngle = MobMovementCircle.getTimeAngle(mob.getTime(), speed, reversed);
        Point2D.Float dir = GameMath.normalize(centerX - (float)mob.getX(), centerY - (float)mob.getY());
        this.angleOffset = GameMath.fixAngle((float)Math.toDegrees(Math.atan2(dir.y, dir.x)) - 90.0f - startAngle);
    }

    @Override
    public void setupPacket(Mob mob, PacketWriter writer) {
        writer.putNextInt(this.range);
        writer.putNextFloat(this.speed);
        writer.putNextFloat(this.angleOffset);
        writer.putNextBoolean(this.reversed);
    }

    @Override
    public void applyPacket(Mob mob, PacketReader reader) {
        this.range = reader.getNextInt();
        this.speed = reader.getNextFloat();
        this.angleOffset = reader.getNextFloat();
        this.reversed = reader.getNextBoolean();
        this.tick(mob);
    }

    public abstract Point2D.Float getCenterPos();

    public float getCurrentAngle(Mob mob) {
        return MobMovementCircle.getTimeAngle(mob.getWorldEntity().getTime(), this.speed, this.reversed) + this.angleOffset;
    }

    public Point2D.Float getCurrentPos(Mob mob) {
        Point2D.Float centerPos = this.getCenterPos();
        if (centerPos != null) {
            Point2D.Float p = MobMovementCircle.getOffsetPositionFloat(mob, this.range, this.speed, this.angleOffset, this.reversed);
            return new Point2D.Float(centerPos.x + p.x, centerPos.y + p.y);
        }
        return null;
    }

    @Override
    public boolean tick(Mob mob) {
        Point2D.Float pos = this.getCurrentPos(mob);
        if (pos != null) {
            this.moveTo(mob, pos.x, pos.y, mob.moveAccuracy);
        }
        return false;
    }

    public static float convertToRotSpeed(int range, float mobSpeed) {
        float circumference = (float)((double)(range * 2) * Math.PI);
        float secondsPerRotation = circumference / mobSpeed;
        return 10.0f / secondsPerRotation * 4.0f;
    }

    public static Point getOffsetPosition(Mob mob, int range, float speed, float angleOffset, boolean reversed) {
        Point2D.Float p = MobMovementCircle.getOffsetPositionFloat(mob, range, speed, angleOffset, reversed);
        return new Point((int)p.x, (int)p.y);
    }

    public static Point2D.Float getOffsetPositionFloat(Mob mob, int range, float speed, float angleOffset, boolean reversed) {
        float angle = MobMovementCircle.getTimeAngle(mob.getWorldEntity().getTime(), speed, reversed);
        double cos = GameMath.cos(angle += angleOffset);
        double sin = GameMath.sin(angle);
        return new Point2D.Float((float)(sin * (double)range), (float)(-(cos * (double)range)));
    }

    public static float getTimeAngle(long time, float speed, boolean reversed) {
        float angle = (float)((double)time / 1000.0 * (double)speed * 36.0 % 360.0);
        if (reversed) {
            return -angle;
        }
        return angle;
    }

    public static float getTimeToAngle(float speed, float angle) {
        return angle / (speed * 36.0f) * 1000.0f;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobMovementCircle) {
            MobMovementCircle other = (MobMovementCircle)obj;
            return this.range == other.range && this.speed == other.speed && this.angleOffset == other.angleOffset && this.reversed == other.reversed;
        }
        return false;
    }
}

