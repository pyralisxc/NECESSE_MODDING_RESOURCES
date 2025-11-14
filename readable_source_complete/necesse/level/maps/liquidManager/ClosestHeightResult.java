/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.liquidManager;

import java.awt.Point;
import java.util.Collection;
import necesse.level.maps.liquidManager.NextHeightTile;

public class ClosestHeightResult {
    public final int startX;
    public final int startY;
    public final Point best;
    public final Point found;
    public final Collection<NextHeightTile> closedTiles;
    public final Collection<NextHeightTile> openTiles;

    public ClosestHeightResult(int startX, int startY, Point best, Point found, Collection<NextHeightTile> closedTiles, Collection<NextHeightTile> openTiles) {
        this.startX = startX;
        this.startY = startY;
        this.best = best;
        this.found = found;
        this.closedTiles = closedTiles;
        this.openTiles = openTiles;
    }
}

