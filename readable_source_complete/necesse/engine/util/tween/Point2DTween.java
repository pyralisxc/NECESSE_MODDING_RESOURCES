/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import java.awt.geom.Point2D;
import necesse.engine.util.tween.ValueTween;

public class Point2DTween
extends ValueTween<Point2D, Point2DTween> {
    public Point2DTween(double duration, Point2D initialValue, Point2D endValue) {
        super(duration, initialValue, endValue);
    }

    public Point2DTween(Point2D initialValue) {
        super(initialValue);
    }

    public Point2DTween(Point2DTween existingTween, double duration, Point2D endValue) {
        super(existingTween, duration, endValue);
    }

    @Override
    protected void tween(double percent) {
        double startX = ((Point2D)this.startValue).getX();
        double startY = ((Point2D)this.startValue).getY();
        double x = startX + percent * (((Point2D)this.endValue).getX() - startX);
        double y = startY + percent * (((Point2D)this.endValue).getY() - startY);
        this.setValue(new Point2D.Double(x, y));
    }

    @Override
    public Point2DTween newTween(double duration, Point2D endValue) {
        return new Point2DTween(this, duration, endValue);
    }
}

