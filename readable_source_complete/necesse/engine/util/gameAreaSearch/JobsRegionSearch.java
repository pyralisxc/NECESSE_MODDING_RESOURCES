/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.GameRegionSearch;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;

public class JobsRegionSearch
extends GameRegionSearch<Iterable<LevelJob>> {
    public JobsRegionSearch(Level level, int startTileX, int startTileY, int maxTileDistance) {
        super(level, level.regionManager.getRegionCoordByTile(startTileX), level.regionManager.getRegionCoordByTile(startTileY), Integer.MAX_VALUE);
        int regionStartX = level.regionManager.getRegionCoordByTile(startTileX - maxTileDistance);
        int regionStartY = level.regionManager.getRegionCoordByTile(startTileY - maxTileDistance);
        int regionEndX = level.regionManager.getRegionCoordByTile(startTileX + maxTileDistance);
        int regionEndY = level.regionManager.getRegionCoordByTile(startTileY + maxTileDistance);
        this.shrinkLimit(regionStartX, regionStartY, regionEndX, regionEndY);
        this.setMaxDistance(Math.max(regionEndX - regionStartX, regionEndY - regionStartY) + 1);
    }

    @Override
    protected Iterable<LevelJob> get(int regionX, int regionY) {
        return this.level.jobsLayer.getJobsInRegion(regionX, regionY);
    }

    public GameAreaStream<LevelJob> streamEach() {
        return this.stream().flatMap(v -> v);
    }
}

