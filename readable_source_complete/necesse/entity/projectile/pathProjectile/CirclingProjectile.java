/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.projectile.pathProjectile.PathProjectile;

public abstract class CirclingProjectile
extends PathProjectile {
    protected float currentAngle;

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.currentAngle);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.currentAngle = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        Point2D.Float pos = this.getAnglePosition();
        this.x = pos.x;
        this.y = pos.y;
    }

    @Override
    public Point2D.Float getPosition(double dist) {
        this.currentAngle = (float)((double)this.currentAngle + dist / (Math.PI * 2 * (double)this.getRadius()) * 360.0);
        return this.getAnglePosition();
    }

    public Point2D.Float getAnglePosition() {
        float x = this.rotatesClockwise() ? -GameMath.sin(this.currentAngle) : GameMath.sin(this.currentAngle);
        float y = GameMath.cos(this.currentAngle);
        Point2D.Float center = this.getCenterPos();
        float radius = this.getRadius();
        return new Point2D.Float(center.x + x * radius, center.y + y * radius);
    }

    public abstract Point2D.Float getCenterPos();

    public abstract float getRadius();

    public boolean rotatesClockwise() {
        return true;
    }
}

