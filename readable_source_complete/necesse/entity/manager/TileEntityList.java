/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.ConcurrentHashMapQueue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.DrawOnMapEntity;
import necesse.entity.TileEntity;
import necesse.entity.manager.EntityComponentManager;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.TileEntityRegionList;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;

public class TileEntityList<T extends TileEntity>
implements Iterable<T> {
    protected final ConcurrentHashMap<Long, T> map = new ConcurrentHashMap();
    protected final ConcurrentHashMapQueue<Long, CachedEntity> cache = new ConcurrentHashMapQueue();
    protected int cacheTTL = 5000;
    protected final EntityManager manager;
    protected final EntityComponentManager<Long> componentManager;
    public final String entityName;
    protected final Function<T, Boolean> isRemoved;
    protected final Consumer<T> dispose;
    protected final Consumer<T> onAdded;
    Consumer<T> onHiddenAdded;
    protected final Consumer<T> onRemoved;
    protected final TileEntityRegionList<T> regionList;

    public TileEntityList(EntityManager manager, EntityComponentManager<Long> componentManager, String entityName, Function<T, Boolean> isRemoved, Consumer<T> dispose, Consumer<T> onAdded, Consumer<T> onRemoved) {
        this.manager = manager;
        this.componentManager = componentManager;
        this.entityName = entityName;
        this.isRemoved = isRemoved;
        this.dispose = dispose;
        this.onAdded = onAdded;
        this.onRemoved = onRemoved;
        this.regionList = new TileEntityRegionList(this.getLevel());
    }

    public TileEntityList(EntityManager manager, EntityComponentManager<Long> componentManager, String entityName, Consumer<T> onAdded, Consumer<T> onRemoved) {
        this(manager, componentManager, entityName, TileEntity::removed, e -> {
            if (!e.isDisposed()) {
                e.dispose();
            }
        }, onAdded, onRemoved);
    }

    protected Level getLevel() {
        return this.manager.level;
    }

    @Override
    public Iterator<T> iterator() {
        return this.map.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.map.values().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.map.values().spliterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream<T> stream() {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.map.values().stream();
        }
    }

    public int count() {
        return this.map.size();
    }

    public int countCache() {
        return this.cache.size();
    }

    protected long getUniqueID(int tileX, int tileY) {
        return GameMath.getUniqueLongKey(tileX, tileY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasEntityID(int tileX, int tileY, boolean searchCache) {
        long uniqueID = this.getUniqueID(tileX, tileY);
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.map.containsKey(uniqueID)) {
                return true;
            }
        }
        if (searchCache) {
            object = this.manager.lock;
            synchronized (object) {
                if (this.cache.containsKey(uniqueID)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T get(int tileX, int tileY, boolean searchCache) {
        Object out;
        long uniqueID = this.getUniqueID(tileX, tileY);
        Object object = this.manager.lock;
        synchronized (object) {
            out = (TileEntity)this.map.get(uniqueID);
            if (out != null) {
                return (T)out;
            }
        }
        if (searchCache) {
            object = this.manager.lock;
            synchronized (object) {
                out = (CachedEntity)this.cache.get(uniqueID);
                if (out != null) {
                    return ((CachedEntity)out).entity;
                }
            }
        }
        return null;
    }

    public void add(T entity) {
        this.addHidden(entity);
        if (this.onAdded != null) {
            this.onAdded.accept(entity);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addHidden(T entity) {
        long uniqueID = this.getUniqueID(((TileEntity)entity).tileX, ((TileEntity)entity).tileY);
        Object object = this.manager.lock;
        synchronized (object) {
            this.map.compute(uniqueID, (uid, last) -> {
                if (last != null && last != entity) {
                    this.runRemoveLogic(last);
                }
                return entity;
            });
            if (this.componentManager != null) {
                this.componentManager.add(uniqueID, entity);
                if (entity instanceof LevelBuffsEntityComponent) {
                    this.manager.level.buffManager.updateBuffs();
                }
            }
        }
        if (!((TileEntity)entity).isInitialized()) {
            ((TileEntity)entity).init();
            ((TileEntity)entity).postInit();
        } else {
            ((TileEntity)entity).onLevelChanged();
        }
        if (this.onHiddenAdded != null) {
            this.onHiddenAdded.accept(entity);
        }
        object = this.manager.lock;
        synchronized (object) {
            this.regionList.updateRegion(entity);
        }
    }

    public TileEntityRegionList<T> getRegionList() {
        return this.regionList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<T> getInRegion(int regionX, int regionY) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.getInRegion(regionX, regionY);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream<T> streamInRegionsShape(Shape shape, int extraRegionRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.streamInRegionsShape(shape, extraRegionRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream<T> streamInRegionsInTileRange(int tileX, int tileY, int tileRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.streamInRegionsInTileRange(tileX, tileY, tileRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameAreaStream<T> streamAreaTileRange(int tileX, int tileY, int tileRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.streamAreaTileRange(tileX, tileY, tileRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<T> getInRegionTileByTile(int tileX, int tileY) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.getInRegionTileByTile(tileX, tileY);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<T> getInRegionRange(int regionX, int regionY, int regionRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.getInRegionRange(regionX, regionY, regionRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<T> getInRegionRangeByTile(int tileX, int tileY, int regionRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.getInRegionRangeByTile(tileX, tileY, regionRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<T> getInRegionByTileRange(int tileX, int tileY, int tileRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            return this.regionList.getInRegionByTileRange(tileX, tileY, tileRange);
        }
    }

    public void frameTick(TickManager tickManager, BiConsumer<T, Float> tickMovement) {
        Level level = this.getLevel();
        if (tickMovement != null) {
            float delta = tickManager.getDelta();
            Performance.record((PerformanceTimerManager)level.tickManager(), "movement", () -> Performance.record((PerformanceTimerManager)level.tickManager(), this.entityName, () -> {
                Object object = this.manager.lock;
                synchronized (object) {
                    for (TileEntity entity : this.map.values()) {
                        if (entity == null || this.isRemoved.apply(entity).booleanValue()) continue;
                        tickMovement.accept(entity, Float.valueOf(delta));
                    }
                }
            }));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clientTick(Consumer<T> entityTick, List<DrawOnMapEntity> drawOnMap) {
        Object object = this.manager.lock;
        synchronized (object) {
            CachedEntity ce;
            while (!this.cache.isEmpty() && (ce = (CachedEntity)this.cache.getFirst()).shouldDie()) {
                this.cache.removeFirst();
            }
        }
        object = this.manager.lock;
        synchronized (object) {
            LinkedList removes = new LinkedList();
            this.map.forEach((? super K uniqueID, ? super V entity) -> {
                if (entity == null) {
                    removes.add(uniqueID);
                } else if (this.isRemoved.apply((TileEntity)entity).booleanValue()) {
                    removes.add(uniqueID);
                    this.runRemoveLogic(entity);
                    if (this.cacheTTL > 0) {
                        this.cache.addLast((Long)uniqueID, new CachedEntity(this, uniqueID.longValue(), entity));
                    }
                } else {
                    if (entity.shouldDrawOnMap()) {
                        drawOnMap.add((DrawOnMapEntity)entity);
                    }
                    entityTick.accept(entity);
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                long uniqueID2 = (Long)iterator.next();
                TileEntity remove = (TileEntity)this.map.remove(uniqueID2);
                if (this.componentManager == null || remove == null) continue;
                this.componentManager.remove(uniqueID2, remove);
                if (!(remove instanceof LevelBuffsEntityComponent)) continue;
                updateBuffs = true;
            }
            if (updateBuffs) {
                this.getLevel().buffManager.updateBuffs();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serverTick(Consumer<T> entityTick, List<DrawOnMapEntity> drawOnMap) {
        Object object = this.manager.lock;
        synchronized (object) {
            CachedEntity ce;
            while (!this.cache.isEmpty() && (ce = (CachedEntity)this.cache.getFirst()).shouldDie()) {
                this.cache.removeFirst();
            }
        }
        object = this.manager.lock;
        synchronized (object) {
            LinkedList removes = new LinkedList();
            this.map.forEach((? super K uniqueID, ? super V entity) -> {
                if (entity == null) {
                    removes.add(uniqueID);
                } else if (this.isRemoved.apply((TileEntity)entity).booleanValue()) {
                    removes.add(uniqueID);
                    this.runRemoveLogic(entity);
                    if (this.cacheTTL > 0) {
                        this.cache.addLast((Long)uniqueID, new CachedEntity(this, uniqueID.longValue(), entity));
                    }
                } else {
                    if (entity.shouldDrawOnMap()) {
                        drawOnMap.add((DrawOnMapEntity)entity);
                    }
                    entityTick.accept(entity);
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                long uniqueID2 = (Long)iterator.next();
                TileEntity remove = (TileEntity)this.map.remove(uniqueID2);
                if (this.componentManager == null || remove == null) continue;
                this.componentManager.remove(uniqueID2, remove);
                if (!(remove instanceof LevelBuffsEntityComponent)) continue;
                updateBuffs = true;
            }
            if (updateBuffs) {
                this.getLevel().buffManager.updateBuffs();
            }
        }
    }

    protected void runRemoveLogic(T entity) {
        if (this.onRemoved != null) {
            this.onRemoved.accept(entity);
        }
        if (!((TileEntity)entity).removed()) {
            ((TileEntity)entity).remove();
        }
        ((TileEntity)entity).onRemovedFromManager();
        if (this.dispose != null) {
            this.dispose.accept(entity);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onLoadingComplete() {
        Object object = this.manager.lock;
        synchronized (object) {
            this.map.values().forEach(TileEntity::onLoadingComplete);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onUnloading() {
        Object object = this.manager.lock;
        synchronized (object) {
            this.map.values().forEach(t -> t.onUnloading(null));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.dispose != null) {
                this.map.values().forEach(this.dispose);
            }
        }
    }

    protected class CachedEntity {
        public final long uniqueID;
        public final T entity;
        public final long endTime;
        final /* synthetic */ TileEntityList this$0;

        /*
         * WARNING - Possible parameter corruption
         * WARNING - void declaration
         */
        public CachedEntity(long entity, T t) {
            void uniqueID;
            this.this$0 = (TileEntityList)this$0;
            this.uniqueID = uniqueID;
            this.entity = entity;
            this.endTime = this$0.getLevel().getTime() + (long)this$0.cacheTTL;
        }

        public boolean shouldDie() {
            return this.endTime <= this.this$0.getLevel().getTime();
        }
    }
}

