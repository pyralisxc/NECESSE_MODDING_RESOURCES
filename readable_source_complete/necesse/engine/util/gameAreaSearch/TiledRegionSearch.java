/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameRegionSearch;
import necesse.level.maps.Level;

public class TiledRegionSearch
extends GameRegionSearch<Point> {
    public TiledRegionSearch(Level level, int startTileX, int startTileY, int maxTileDistance) {
        super(level, level.regionManager.getRegionCoordByTile(startTileX), level.regionManager.getRegionCoordByTile(startTileY), Integer.MAX_VALUE);
        int regionStartX = level.regionManager.getRegionCoordByTile(startTileX - maxTileDistance - 1);
        int regionStartY = level.regionManager.getRegionCoordByTile(startTileY - maxTileDistance - 1);
        int regionEndX = level.regionManager.getRegionCoordByTile(startTileX + maxTileDistance + 1);
        int regionEndY = level.regionManager.getRegionCoordByTile(startTileY + maxTileDistance + 1);
        this.shrinkLimit(regionStartX, regionStartY, regionEndX, regionEndY);
        this.setMaxDistance(Math.max(regionEndX - regionStartX, regionEndY - regionStartY) + 1);
    }

    @Override
    protected Point get(int regionX, int regionY) {
        return new Point(regionX, regionY);
    }
}

