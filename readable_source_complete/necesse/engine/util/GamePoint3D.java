/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.geom.Point2D;

public class GamePoint3D {
    public float x;
    public float y;
    public float height;

    public GamePoint3D(float x, float y, float height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public GamePoint3D(Point2D.Float point, float height) {
        this(point.x, point.y, height);
    }

    public float distF(float x, float y, float height) {
        return (float)this.dist(x, y, height);
    }

    public float distF(GamePoint3D point) {
        return (float)this.dist(point);
    }

    public double dist(float x, float y, float height) {
        return Math.sqrt((x -= this.x) * x + (y -= this.y) * y + (height -= this.height) * height);
    }

    public double dist(GamePoint3D point) {
        return this.dist(point.x, point.y, point.height);
    }

    public GamePoint3D normalize() {
        float dist = this.distF(0.0f, 0.0f, 0.0f);
        float normX = dist == 0.0f ? 0.0f : this.x / dist;
        float normY = dist == 0.0f ? 0.0f : this.y / dist;
        float normHeight = dist == 0.0f ? 0.0f : this.height / dist;
        return new GamePoint3D(normX, normY, normHeight);
    }

    public GamePoint3D normalizeTo(float x, float y, float height) {
        return new GamePoint3D(x - this.x, y - this.y, height - this.height).normalize();
    }

    public GamePoint3D dirFromLength(float x, float y, float height, float length) {
        GamePoint3D dir = this.normalizeTo(x, y, height);
        return new GamePoint3D(this.x + dir.x * length, this.y + dir.y * length, this.height + dir.height * length);
    }
}

