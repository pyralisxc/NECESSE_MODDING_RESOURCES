/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.GameMath;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.GameRegionSearch;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionTrackerGetter;

public class RegionTrackerListRegionSearch<T extends RegionTrackerGetter<? super T>>
extends GameRegionSearch<Iterable<T>> {
    private final RegionTrackerList<T> entityRegionList;

    public RegionTrackerListRegionSearch(Level level, RegionTrackerList<T> entityRegionList, float startX, float startY, int maxTileDistance) {
        super(level, level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(startX)), level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(startY)), Integer.MAX_VALUE);
        this.entityRegionList = entityRegionList;
        int startTileX = GameMath.getTileCoordinate(startX);
        int startTileY = GameMath.getTileCoordinate(startY);
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

