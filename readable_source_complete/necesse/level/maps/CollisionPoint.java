/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;

public class CollisionPoint<T> {
    public final T target;
    public final Rectangle rectangle;
    public final Line2D line;
    public final boolean checkInsideRect;
    private boolean calculatedPoint = false;
    private IntersectionPoint<T> point;

    public CollisionPoint(T target, Rectangle rectangle, Line2D line, boolean checkInsideRect) {
        this.target = target;
        this.rectangle = rectangle;
        this.line = line;
        this.checkInsideRect = checkInsideRect;
    }

    public IntersectionPoint<T> getPoint() {
        if (!this.calculatedPoint) {
            this.point = GameMath.getIntersectionPoint(this.target, this.line, this.rectangle, this.checkInsideRect);
            this.calculatedPoint = true;
        }
        return this.point;
    }

    public static <T extends Rectangle> IntersectionPoint<T> getClosestCollision(List<T> col, Line2D l, boolean checkInsideRect) {
        if (col.isEmpty()) {
            return null;
        }
        return CollisionPoint.getClosestCollision(col.stream(), l, checkInsideRect);
    }

    public static <T extends Rectangle> IntersectionPoint<T> getClosestCollision(Stream<T> col, Line2D l, boolean checkInsideRect) {
        return col.map(r -> new CollisionPoint<Rectangle>((Rectangle)r, (Rectangle)r, l, checkInsideRect)).sorted(Comparator.comparing(cp -> l.getP1().distance(cp.rectangle.getCenterX(), cp.rectangle.getCenterY()))).filter(cp -> cp.getPoint() != null).map(CollisionPoint::getPoint).findFirst().orElse(null);
    }
}

