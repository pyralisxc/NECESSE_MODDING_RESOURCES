/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.geom.Line2D;
import necesse.engine.util.IntersectionPoint;

public class Ray<T>
extends Line2D.Double {
    public final double dist = this.getP1().distance(this.getP2());
    public final T targetHit;
    public final IntersectionPoint.Dir targetHitDir;

    public Ray(double x1, double y1, double x2, double y2, T targetHit, IntersectionPoint.Dir targetHitDir) {
        super(x1, y1, x2, y2);
        this.targetHit = targetHit;
        this.targetHitDir = targetHitDir;
    }

    public IntersectionPoint<T> getIntersectionPoint() {
        return new IntersectionPoint<T>(this.x2, this.y2, this.targetHit, this.targetHitDir);
    }
}

