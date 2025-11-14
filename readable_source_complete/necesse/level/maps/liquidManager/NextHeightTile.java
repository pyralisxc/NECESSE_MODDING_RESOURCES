/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.liquidManager;

import java.awt.Point;

class NextHeightTile
extends Point {
    public final int height;
    public int sameHeightTraveled;

    public NextHeightTile(int x, int y, int height, int sameHeightTraveled) {
        super(x, y);
        this.height = height;
        this.sameHeightTraveled = sameHeightTraveled;
    }
}

