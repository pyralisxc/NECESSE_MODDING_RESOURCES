/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.GameMath;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.GameRegionSearch;
import necesse.entity.Entity;
import necesse.entity.manager.EntityRegionList;
import necesse.level.maps.Level;

public class EntityListRegionSearch<T extends Entity>
extends GameRegionSearch<Iterable<T>> {
    private final EntityRegionList<T> entityRegionList;

    public EntityListRegionSearch(Level level, EntityRegionList<T> entityRegionList, float startX, float startY, int maxTileDistance) {
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

