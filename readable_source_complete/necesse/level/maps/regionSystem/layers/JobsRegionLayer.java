/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.awt.Point;
import java.util.Collection;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashMapPointEntry;
import necesse.engine.util.ShortPointHashMap;
import necesse.engine.util.ShortPointHashSet;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.SaveDataRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.ServerTickRegionLayer;

public class JobsRegionLayer
extends RegionLayer
implements SaveDataRegionLayer,
ServerTickRegionLayer {
    public static int secondsToTickLevel = 180;
    private final ShortPointHashMap<GameLinkedList<LevelJob>> jobs = new ShortPointHashMap();
    private double tilesPerJobTick;
    private final double nextTilesPerJobTick;
    private double tileJobTickBuffer;
    private int currentJobTileTickX;
    private int currentJobTileTickY;

    public JobsRegionLayer(Region region) {
        super(region);
        this.tilesPerJobTick = (double)(region.tileWidth * region.tileHeight) / 20.0 / 10.0;
        this.nextTilesPerJobTick = (double)(region.tileWidth * region.tileHeight) / 20.0 / (double)secondsToTickLevel;
    }

    @Override
    public void init() {
    }

    @Override
    public void onLayerLoaded() {
    }

    @Override
    public void onLoadingComplete() {
    }

    @Override
    public void onLayerUnloaded() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSaveData(SaveData save) {
        JobsRegionLayer jobsRegionLayer = this;
        synchronized (jobsRegionLayer) {
            SaveData jobsSave = new SaveData("JOBS");
            for (HashMapPointEntry<Point, GameLinkedList<LevelJob>> entry : this.jobs.getEntries()) {
                GameLinkedList<LevelJob> list = entry.getValue();
                for (LevelJob job : list) {
                    if (!job.shouldSave() || !job.isValid()) continue;
                    SaveData jobSave = new SaveData(job.getStringID());
                    job.addSaveData(jobSave);
                    jobsSave.addSaveData(jobSave);
                }
            }
            if (!jobsSave.isEmpty()) {
                save.addSaveData(jobsSave);
            }
        }
    }

    @Override
    public void loadSaveData(LoadData save) {
        LoadData jobsSave = save.getFirstLoadDataByName("JOBS");
        if (jobsSave != null) {
            for (LoadData jobSave : jobsSave.getLoadData()) {
                if (!jobSave.isArray()) continue;
                String stringID = jobSave.getName();
                LevelJob job = LevelJobRegistry.loadJob(stringID, jobSave);
                try {
                    this.addJob(job, false, true);
                }
                catch (Exception e) {
                    GameLog.warn.println("Could not load level job");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void serverTick() {
        this.tileJobTickBuffer += this.tilesPerJobTick;
        if (this.tileJobTickBuffer >= 1.0) {
            int ticks = (int)this.tileJobTickBuffer;
            this.tileJobTickBuffer -= (double)ticks;
            for (int i = 0; i < ticks; ++i) {
                this.region.layers.addLevelJobs(this, this.currentJobTileTickX, this.currentJobTileTickY);
                ++this.currentJobTileTickX;
                if (this.currentJobTileTickX < this.region.tileWidth) continue;
                this.currentJobTileTickX = 0;
                ++this.currentJobTileTickY;
                if (this.currentJobTileTickY < this.region.tileHeight) continue;
                this.currentJobTileTickY = 0;
                this.tilesPerJobTick = this.nextTilesPerJobTick;
                this.cleanInvalidJobs();
            }
        }
    }

    public Iterable<LevelJob> getJobsInRegion() {
        return () -> this.streamJobsInRegion().iterator();
    }

    public Stream<LevelJob> streamJobsInRegion() {
        return this.jobs.values().stream().flatMap(Collection::stream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<LevelJob> getTileJobsInRegion(int regionTileX, int regionTileY) {
        JobsRegionLayer jobsRegionLayer = this;
        synchronized (jobsRegionLayer) {
            GameLinkedList<LevelJob> list = this.jobs.get(regionTileX, regionTileY);
            if (list == null) {
                list = new GameLinkedList();
                this.jobs.put(regionTileX, regionTileY, list);
            }
            return list;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cleanTileIfEmptyInRegion(int regionTileX, int regionTileY) {
        JobsRegionLayer jobsRegionLayer = this;
        synchronized (jobsRegionLayer) {
            GameLinkedList<LevelJob> list = this.jobs.get(regionTileX, regionTileY);
            if (list != null && list.isEmpty()) {
                this.jobs.remove(regionTileX, regionTileY);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanInvalidJobs() {
        JobsRegionLayer jobsRegionLayer = this;
        synchronized (jobsRegionLayer) {
            ShortPointHashSet removes = new ShortPointHashSet();
            for (HashMapPointEntry<Point, GameLinkedList<LevelJob>> entry : this.jobs.getEntries()) {
                GameLinkedList<LevelJob> list = entry.getValue();
                if (!list.removeIf(j -> !j.isValid()) || !list.isEmpty()) continue;
                removes.add(entry.getX(), entry.getY());
            }
            for (Point regionTile : removes) {
                this.jobs.remove(regionTile.x, regionTile.y);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LevelJob addJob(LevelJob job, boolean override, boolean forceAdd) {
        JobsRegionLayer jobsRegionLayer = this;
        synchronized (jobsRegionLayer) {
            GameLinkedList<LevelJob> list = this.getTileJobsInRegion(job.tileX - this.region.tileXOffset, job.tileY - this.region.tileYOffset);
            if (!forceAdd) {
                GameLinkedList.Element current = list.getFirstElement();
                while (current != null) {
                    if (!((LevelJob)current.object).isValid()) {
                        GameLinkedList.Element last = current;
                        current = current.next();
                        last.remove();
                        continue;
                    }
                    if (((LevelJob)current.object).isSameJob(job)) {
                        if (override) {
                            current.remove();
                            break;
                        }
                        return (LevelJob)current.object;
                    }
                    current = current.next();
                }
            }
            GameLinkedList.Element element = list.addLast(job);
            job.init(this.level, element);
            if (!forceAdd && !job.isValid()) {
                element.remove();
                if (list.isEmpty()) {
                    this.jobs.remove(job.tileX - this.region.tileXOffset, job.tileY - this.region.tileYOffset);
                }
                return null;
            }
            return job;
        }
    }
}

