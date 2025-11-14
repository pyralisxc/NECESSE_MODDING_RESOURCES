/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.path;

import java.awt.Point;

public enum PathDir {
    UP(0, -1),
    UP_RIGHT(1, -1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, 1),
    DOWN(0, 1),
    DOWN_LEFT(-1, 1),
    LEFT(-1, 0),
    UP_LEFT(-1, -1);

    public final Point point;
    public final int x;
    public final int y;
    public final boolean isDiagonal;
    public final int dir;

    private PathDir(int x, int y) {
        if (Math.abs(x) > 1 || Math.abs(y) > 1) {
            throw new IllegalArgumentException("Cannot be offset more than 1");
        }
        this.point = new Point(x, y);
        this.x = x;
        this.y = y;
        boolean bl = this.isDiagonal = x != 0 && y != 0;
        this.dir = x == 0 && y < 0 ? 0 : (x > 0 && y < 0 ? 1 : (x > 0 && y == 0 ? 2 : (x > 0 ? 3 : (x == 0 && y > 0 ? 4 : (x < 0 && y > 0 ? 5 : (x < 0 && y == 0 ? 6 : 7))))));
    }

    public static PathDir getDir(Point firstPoint, Point nextPoint) {
        int x = firstPoint.x - nextPoint.x;
        int y = firstPoint.y - nextPoint.y;
        if (x == 0 && y < 0) {
            return UP;
        }
        if (x > 0 && y < 0) {
            return UP_RIGHT;
        }
        if (x > 0 && y == 0) {
            return RIGHT;
        }
        if (x > 0) {
            return DOWN_RIGHT;
        }
        if (x == 0 && y > 0) {
            return DOWN;
        }
        if (x < 0 && y > 0) {
            return DOWN_LEFT;
        }
        if (x < 0 && y == 0) {
            return LEFT;
        }
        if (x < 0) {
            return UP_LEFT;
        }
        return null;
    }
}

