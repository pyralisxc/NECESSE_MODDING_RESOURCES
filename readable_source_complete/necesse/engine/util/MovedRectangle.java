/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import necesse.entity.mobs.Mob;

public class MovedRectangle
extends Polygon {
    public MovedRectangle(Mob mob, int endX, int endY) {
        this(mob.getCollision(), endX - mob.getX(), endY - mob.getY());
    }

    public MovedRectangle(Mob mob, int startX, int startY, int endX, int endY) {
        this(mob.getCollision(startX, startY), endX - startX, endY - startY);
    }

    public MovedRectangle(Rectangle rect, int startX, int startY, int endX, int endY) {
        this(rect, endX - startX, endY - startY);
    }

    public MovedRectangle(Rectangle start, int deltaX, int deltaY) {
        Rectangle end = new Rectangle(start.x + deltaX, start.y + deltaY, start.width, start.height);
        if (deltaX > 0) {
            if (deltaY > 0) {
                this.build(MovedRectangle.botLeft(start), MovedRectangle.topLeft(start), MovedRectangle.topRight(start), MovedRectangle.topRight(end), MovedRectangle.botRight(end), MovedRectangle.botLeft(end));
            } else if (deltaY < 0) {
                this.build(MovedRectangle.botRight(start), MovedRectangle.botLeft(start), MovedRectangle.topLeft(start), MovedRectangle.topLeft(end), MovedRectangle.topRight(end), MovedRectangle.botRight(end));
            } else {
                this.build(MovedRectangle.botLeft(start), MovedRectangle.topLeft(start), MovedRectangle.topRight(end), MovedRectangle.botRight(end));
            }
        } else if (deltaX < 0) {
            if (deltaY > 0) {
                this.build(MovedRectangle.topLeft(start), MovedRectangle.topRight(start), MovedRectangle.botRight(start), MovedRectangle.botRight(end), MovedRectangle.botLeft(end), MovedRectangle.topLeft(end));
            } else if (deltaY < 0) {
                this.build(MovedRectangle.topRight(start), MovedRectangle.botRight(start), MovedRectangle.botLeft(start), MovedRectangle.botLeft(end), MovedRectangle.topLeft(end), MovedRectangle.topRight(end));
            } else {
                this.build(MovedRectangle.topRight(start), MovedRectangle.botRight(start), MovedRectangle.botLeft(end), MovedRectangle.topLeft(end));
            }
        } else if (deltaY > 0) {
            this.build(MovedRectangle.topLeft(start), MovedRectangle.topRight(start), MovedRectangle.botRight(end), MovedRectangle.botLeft(end));
        } else if (deltaY < 0) {
            this.build(MovedRectangle.botLeft(start), MovedRectangle.botRight(start), MovedRectangle.topRight(end), MovedRectangle.topLeft(end));
        } else {
            this.build(MovedRectangle.topLeft(start), MovedRectangle.topRight(start), MovedRectangle.botRight(start), MovedRectangle.botLeft(start));
        }
    }

    private void build(Point ... points) {
        this.xpoints = new int[points.length];
        this.ypoints = new int[points.length];
        this.npoints = points.length;
        for (int i = 0; i < points.length; ++i) {
            this.xpoints[i] = points[i].x;
            this.ypoints[i] = points[i].y;
        }
    }

    private static Point topLeft(Rectangle rectangle) {
        return new Point(rectangle.x, rectangle.y);
    }

    private static Point topRight(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width, rectangle.y);
    }

    private static Point botLeft(Rectangle rectangle) {
        return new Point(rectangle.x, rectangle.y + rectangle.height);
    }

    private static Point botRight(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
    }
}

