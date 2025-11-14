/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.Iterator;

public class BoundsPointIterator
implements Iterator<Point> {
    public final int startX;
    public final int endX;
    public final int startY;
    public final int endY;
    private int currentX;
    private int currentY;

    public BoundsPointIterator(int startX, int endX, int startY, int endY) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.currentX = startX;
        this.currentY = startY;
    }

    @Override
    public boolean hasNext() {
        return this.currentX <= this.endX && this.currentY <= this.endY;
    }

    @Override
    public Point next() {
        Point out = new Point(this.currentX, this.currentY);
        ++this.currentX;
        if (this.currentX > this.endX) {
            this.currentX = this.startX;
            ++this.currentY;
        }
        return out;
    }
}

