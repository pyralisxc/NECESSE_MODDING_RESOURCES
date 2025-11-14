/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.projectile.pathProjectile.CirclingProjectile;

public abstract class PositionedCirclingProjectile
extends CirclingProjectile {
    protected float centerX;
    protected float centerY;

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.centerX);
        writer.putNextFloat(this.centerY);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.centerX = reader.getNextFloat();
        this.centerY = reader.getNextFloat();
    }

    @Override
    public Point2D.Float getCenterPos() {
        return new Point2D.Float(this.centerX, this.centerY);
    }
}

