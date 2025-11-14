/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovementSpiral;

public class MobMovementSpiralLevelPos
extends MobMovementSpiral {
    public float centerX;
    public float centerY;

    public MobMovementSpiralLevelPos() {
    }

    public MobMovementSpiralLevelPos(Mob mob, float centerX, float centerY, float outerRadius, int semiCircles, float radiusDecreasePerSemiCircle, float speed, float startAngle, boolean clockwise) {
        super(mob, outerRadius, semiCircles, radiusDecreasePerSemiCircle, speed, startAngle, clockwise);
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public MobMovementSpiralLevelPos(Mob mob, float centerX, float centerY, float outerRadius, int semiCircles, float radiusDecreasePerSemiCircle, float speed, boolean clockwise) {
        super(mob, centerX, centerY, outerRadius, semiCircles, radiusDecreasePerSemiCircle, speed, clockwise);
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public void setupPacket(Mob mob, PacketWriter writer) {
        writer.putNextFloat(this.centerX);
        writer.putNextFloat(this.centerY);
        super.setupPacket(mob, writer);
    }

    @Override
    public void applyPacket(Mob mob, PacketReader reader) {
        this.centerX = reader.getNextFloat();
        this.centerY = reader.getNextFloat();
        super.applyPacket(mob, reader);
    }

    @Override
    public Point2D.Float getCenterPos() {
        return new Point2D.Float(this.centerX, this.centerY);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobMovementSpiralLevelPos) {
            MobMovementSpiralLevelPos other = (MobMovementSpiralLevelPos)obj;
            return this.centerX == other.centerX && this.centerY == other.centerY && super.equals(obj);
        }
        return false;
    }
}

