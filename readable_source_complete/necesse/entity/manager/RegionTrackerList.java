/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.RegionTrackerListRegionSearch;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.LevelRegionsSpliterator;
import necesse.level.maps.regionSystem.RegionTracker;
import necesse.level.maps.regionSystem.RegionTrackerGetter;

public class RegionTrackerList<T extends RegionTrackerGetter<? super T>> {
    protected final Level level;
    protected final HashMap<Long, GameLinkedList<? extends T>> regionEntities;
    protected final HashMap<Long, GameLinkedList<? extends T>> saveToRegionEntities;
    protected final GameLinkedList<? extends T> noRegionEntities;

    public RegionTrackerList(Level level) {
        this.level = level;
        this.regionEntities = new HashMap();
        this.saveToRegionEntities = new HashMap();
        this.noRegionEntities = new GameLinkedList();
    }

    public int getSize() {
        return this.regionEntities.size();
    }

    public PointSetAbstract<?> updateRegion(T entity) {
        RegionTracker<? extends T> tracker = entity.getRegionTracker();
        if (tracker != null) {
            return tracker.updateRegion(this::getList, this::getSaveToRegionList, this.noRegionEntities);
        }
        return null;
    }

    private GameLinkedList<? super T> getList(int regionX, int regionY) {
        return this.regionEntities.computeIfAbsent(GameMath.getUniqueLongKey(regionX, regionY), key -> new GameLinkedList<T>((Long)key){
            final /* synthetic */ Long val$key;
            {
                this.val$key = l;
            }

            @Override
            public void onRemoved(GameLinkedList.Element element) {
                super.onRemoved(element);
                if (this.isEmpty()) {
                    RegionTrackerList.this.regionEntities.remove(this.val$key);
                }
            }
        });
    }

    private GameLinkedList<? super T> getSaveToRegionList(int regionX, int regionY) {
        GameLinkedList list = null;
        if (this.level.regionManager.isRegionLoaded(regionX, regionY)) {
            list = this.saveToRegionEntities.computeIfAbsent(GameMath.getUniqueLongKey(regionX, regionY), key -> new GameLinkedList<T>((Long)key){
                final /* synthetic */ Long val$key;
                {
                    this.val$key = l;
                }

                @Override
                public void onRemoved(GameLinkedList.Element element) {
                    super.onRemoved(element);
                    if (this.isEmpty()) {
                        RegionTrackerList.this.saveToRegionEntities.remove(this.val$key);
                    }
                }
            });
        }
        return list;
    }

    public GameLinkedList<T> getSaveToRegion(int regionX, int regionY) {
        GameLinkedList<? extends T> out = this.saveToRegionEntities.get(GameMath.getUniqueLongKey(regionX, regionY));
        if (out == null) {
            return new GameLinkedList();
        }
        return out;
    }

    public GameLinkedList<T> getInRegion(int regionX, int regionY) {
        GameLinkedList<? extends T> out = this.regionEntities.get(GameMath.getUniqueLongKey(regionX, regionY));
        if (out == null) {
            return new GameLinkedList();
        }
        return out;
    }

    public GameLinkedList<T> getInNoRegion() {
        return this.noRegionEntities;
    }

    public Stream<T> streamInRegionsShape(Shape shape, int extraRegionRange) {
        return new LevelRegionsSpliterator(this.level, shape, extraRegionRange).stream().flatMap(rp -> this.getInRegion(rp.x, rp.y).stream());
    }

    public Stream<T> streamInRegionsInRange(float x, float y, int range) {
        return this.streamInRegionsShape(GameUtils.rangeBounds(x, y, range), 0);
    }

    public Stream<T> streamInRegionsInTileRange(int x, int y, int tileRange) {
        return this.streamInRegionsShape(GameUtils.rangeTileBounds(x, y, tileRange), 0);
    }

    public GameAreaStream<T> streamArea(float x, float y, int range) {
        return this.streamAreaTileRange((int)x, (int)y, GameMath.getTileCoordinate(range) + 1);
    }

    public GameAreaStream<T> streamAreaTileRange(int x, int y, int tileRange) {
        return new RegionTrackerListRegionSearch(this.level, this, x, y, tileRange).streamEach();
    }

    public GameLinkedList<T> getInRegionTileByTile(int tileX, int tileY) {
        return this.getInRegion(this.level.regionManager.getRegionCoordByTile(tileX), this.level.regionManager.getRegionCoordByTile(tileY));
    }

    public ArrayList<T> getInRegionRange(int regionX, int regionY, int regionRange) {
        ArrayList<T> out = new ArrayList<T>();
        for (int x = regionX - regionRange; x <= regionX + regionRange; ++x) {
            if (!this.level.regionManager.isRegionXWithinBounds(x)) continue;
            for (int y = regionY - regionRange; y <= regionY + regionRange; ++y) {
                if (!this.level.regionManager.isRegionYWithinBounds(y)) continue;
                out.addAll(this.getInRegion(x, y));
            }
        }
        return out;
    }

    public ArrayList<T> getInRegionRangeByTile(int tileX, int tileY, int regionRange) {
        int regionX = this.level.regionManager.getRegionCoordByTile(tileX);
        int regionY = this.level.regionManager.getRegionCoordByTile(tileY);
        return this.getInRegionRange(regionX, regionY, regionRange);
    }

    public ArrayList<T> getInRegionByTileRange(int tileX, int tileY, int tileRange) {
        int regionRange = Math.max(1, this.level.regionManager.getRegionCoordByTile(tileRange) + 1);
        int regionX = this.level.regionManager.getRegionCoordByTile(tileX);
        int regionY = this.level.regionManager.getRegionCoordByTile(tileY);
        return this.getInRegionRange(regionX, regionY, regionRange);
    }

    @FunctionalInterface
    public static interface RegionListGetter<T> {
        public GameLinkedList<? super T> getList(int var1, int var2);
    }
}

