/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.geom.Point2D;

public class IntersectionPoint<T>
extends Point2D.Double {
    public final T target;
    public final Dir dir;

    public IntersectionPoint(double x, double y, T target, Dir dir) {
        super(x, y);
        this.target = target;
        this.dir = dir;
    }

    public static enum Dir {
        UP,
        RIGHT,
        DOWN,
        LEFT;

    }
}

