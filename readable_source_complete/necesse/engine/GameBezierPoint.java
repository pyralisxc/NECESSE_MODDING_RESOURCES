/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.awt.Point;
import java.awt.geom.Point2D;

public class GameBezierPoint {
    public float startX;
    public float startY;
    public float targetX;
    public float targetY;

    public GameBezierPoint(float startX, float startY, float targetX, float targetY) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public GameBezierPoint(Point2D.Float startPos, Point2D.Float targetPos) {
        this(startPos.x, startPos.y, targetPos.x, targetPos.y);
    }

    public GameBezierPoint(Point startPos, Point targetPos) {
        this(startPos.x, startPos.y, targetPos.x, targetPos.y);
    }

    public float getPointXOnCurve(GameBezierPoint targetPoint, float progress) {
        return (float)(Math.pow(1.0f - progress, 3.0) * (double)this.startX + (double)(3.0f * progress) * Math.pow(1.0f - progress, 2.0) * (double)this.targetX + 3.0 * Math.pow(progress, 2.0) * (double)(1.0f - progress) * (double)targetPoint.targetX + Math.pow(progress, 3.0) * (double)targetPoint.startX);
    }

    public float getPointYOnCurve(GameBezierPoint targetPoint, float progress) {
        return (float)(Math.pow(1.0f - progress, 3.0) * (double)this.startY + (double)(3.0f * progress) * Math.pow(1.0f - progress, 2.0) * (double)this.targetY + 3.0 * Math.pow(progress, 2.0) * (double)(1.0f - progress) * (double)targetPoint.targetY + Math.pow(progress, 3.0) * (double)targetPoint.startY);
    }
}

