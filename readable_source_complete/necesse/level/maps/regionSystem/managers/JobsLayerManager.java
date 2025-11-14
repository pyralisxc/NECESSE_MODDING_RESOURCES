/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.managers;

import java.awt.Shape;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.JobsRegionSearch;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.regionSystem.LevelRegionsSpliterator;
import necesse.level.maps.regionSystem.Region;

public class JobsLayerManager {
    protected final Level level;

    public JobsLayerManager(Level level) {
        this.level = level;
    }

    public Iterable<LevelJob> getJobsInRegion(int regionX, int regionY) {
        Region region = this.level.regionManager.getRegion(regionX, regionY, false);
        if (region == null) {
            return new GameLinkedList<LevelJob>();
        }
        return region.jobsLayer.getJobsInRegion();
    }

    public Stream<LevelJob> streamJobsInRegion(int regionX, int regionY) {
        Region region = this.level.regionManager.getRegion(regionX, regionY, false);
        if (region == null) {
            return Stream.empty();
        }
        return region.jobsLayer.streamJobsInRegion();
    }

    public Stream<LevelJob> streamJobsInTile(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return Stream.empty();
        }
        return region.jobsLayer.getTileJobsInRegion(tileX - region.tileXOffset, tileY - region.tileYOffset).stream();
    }

    public GameAreaStream<LevelJob> streamAreaJobs(int tileX, int tileY, int maxTileDistance) {
        return new JobsRegionSearch(this.level, tileX, tileY, maxTileDistance).streamEach();
    }

    public Stream<LevelJob> streamJobsInRegionsShape(Shape shape, int extraRegionRange) {
        return new LevelRegionsSpliterator(this.level, shape, extraRegionRange).stream().flatMap(rp -> this.streamJobsInRegion(rp.x, rp.y)).filter(LevelJob::isValid);
    }

    public Stream<LevelJob> streamInRegionsInRange(float levelX, float levelY, int range) {
        return this.streamJobsInRegionsShape(GameUtils.rangeBounds(levelX, levelY, range), 0);
    }

    public Stream<LevelJob> streamInRegionsInTileRange(int x, int y, int tileRange) {
        return this.streamJobsInRegionsShape(GameUtils.rangeTileBounds(x, y, tileRange), 0);
    }

    public LevelJob addJob(LevelJob job, boolean override, boolean forceAdd) {
        Region region = this.level.regionManager.getRegionByTile(job.tileX, job.tileY, forceAdd);
        if (region == null) {
            return null;
        }
        return region.jobsLayer.addJob(job, override, forceAdd);
    }

    public LevelJob addJob(LevelJob job, boolean override) {
        return this.addJob(job, override, false);
    }

    public LevelJob addJob(LevelJob job) {
        return this.addJob(job, false);
    }
}

