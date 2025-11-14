/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.trails;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;

public class TrailVector {
    public final Point2D.Float pos;
    public final float dx;
    public final float dy;
    public final float thickness;
    public final float height;

    public TrailVector(TrailVector copy) {
        this.pos = new Point2D.Float(copy.pos.x, copy.pos.y);
        this.dx = copy.dx;
        this.dy = copy.dy;
        this.thickness = copy.thickness;
        this.height = copy.height;
    }

    public TrailVector(Point2D.Float pos, float dx, float dy, float thickness, float height) {
        this.pos = pos;
        Point2D.Float normalize = GameMath.normalize(dx, dy);
        this.dx = normalize.x;
        this.dy = normalize.y;
        this.thickness = thickness;
        this.height = height;
    }

    public TrailVector(float x, float y, float dx, float dy, float thickness, float height) {
        this(new Point2D.Float(x, y), dx, dy, thickness, height);
    }

    public float getAngle() {
        return (float)Math.toDegrees(Math.atan2(this.dy, this.dx));
    }

    public boolean isSame(TrailVector other) {
        return this.pos.x == other.pos.x && this.pos.y == other.pos.y && this.dx == other.dx && this.dy == other.dy && this.thickness == other.thickness;
    }
}

