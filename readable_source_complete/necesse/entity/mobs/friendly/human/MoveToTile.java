/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import java.awt.Point;

public abstract class MoveToTile
extends Point {
    public boolean acceptAdjacentTiles;

    public MoveToTile(int x, int y, boolean acceptAdjacentTiles) {
        super(x, y);
        this.acceptAdjacentTiles = acceptAdjacentTiles;
    }

    public MoveToTile(Point p, boolean acceptAdjacentTiles) {
        super(p);
        this.acceptAdjacentTiles = acceptAdjacentTiles;
    }

    public abstract boolean moveIfPathFailed(float var1);

    public abstract boolean isAtLocation(float var1, boolean var2);

    public abstract void onArrivedAtLocation();
}

