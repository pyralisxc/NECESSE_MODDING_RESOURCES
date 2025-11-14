/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.GameRegionSearch;
import necesse.entity.Entity;
import necesse.entity.manager.EntityRegionList;
import necesse.level.maps.Level;

public class EntityListsRegionSearch<T extends Entity>
extends GameRegionSearch<Stream<? extends T>> {
    private final EntityRegionList<? extends T>[] entityRegionLists;

    public EntityListsRegionSearch(Level level, float startX, float startY, int maxTileDistance, EntityRegionList<? extends T> ... entityRegionLists) {
        super(level, level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(startX)), level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(startY)), Integer.MAX_VALUE);
        this.entityRegionLists = entityRegionLists;
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
    protected Stream<? extends T> get(int regionX, int regionY) {
        Stream out = null;
        for (EntityRegionList<T> entityRegionList : this.entityRegionLists) {
            out = out == null ? entityRegionList.getInRegion(regionX, regionY).stream() : Stream.concat(out, entityRegionList.getInRegion(regionX, regionY).stream());
        }
        return out;
    }

    public GameAreaStream<T> streamEach() {
        return this.stream().flatStream(v -> v);
    }
}

