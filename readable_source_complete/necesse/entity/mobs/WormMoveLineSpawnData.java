/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.geom.Point2D;

public class WormMoveLineSpawnData {
    public Point2D.Float lastPoint;
    public float movedDist = 0.0f;

    public WormMoveLineSpawnData(float startX, float startY) {
        this.lastPoint = new Point2D.Float(startX, startY);
    }
}

