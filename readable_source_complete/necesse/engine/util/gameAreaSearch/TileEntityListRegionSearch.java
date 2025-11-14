/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.GameRegionSearch;
import necesse.entity.TileEntity;
import necesse.entity.manager.TileEntityRegionList;
import necesse.level.maps.Level;

public class TileEntityListRegionSearch<T extends TileEntity>
extends GameRegionSearch<Iterable<T>> {
    private final TileEntityRegionList<T> entityRegionList;

    public TileEntityListRegionSearch(Level level, TileEntityRegionList<T> entityRegionList, int startTileX, int startTileY, int maxTileDistance) {
        super(level, level.regionManager.getRegionCoordByTile(startTileX), level.regionManager.getRegionCoordByTile(startTileY), Integer.MAX_VALUE);
        this.entityRegionList = entityRegionList;
        int regionStartX = level.regionManager.getRegionCoordByTile(startTileX - maxTileDistance - 1);
        int regionStartY = level.regionManager.getRegionCoordByTile(startTileY - maxTileDistance - 1);
        int regionEndX = level.regionManager.getRegionCoordByTile(startTileX + maxTileDistance + 1);
        int regionEndY = level.regionManager.getRegionCoordByTile(startTileY + maxTileDistance + 1);
        this.shrinkLimit(regionStartX, regionStartY, regionEndX, regionEndY);
        this.setMaxDistance(Math.max(regionEndX - regionStartX, regionEndY - regionStartY) + 1);
    }

    @Override
    protected Iterable<T> get(int regionX, int regionY) {
        return this.entityRegionList.getInRegion(regionX, regionY);
    }

    public GameAreaStream<T> streamEach() {
        return this.stream().flatMap(v -> v);
    }
}

