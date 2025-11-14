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
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.ConcurrentHashMapQueue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.DrawOnMapEntity;
import necesse.entity.Entity;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.EntityRegionList;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;

public class EntityList<T extends Entity>
implements Iterable<T> {
    protected final ConcurrentHashMap<Integer, T> map = new ConcurrentHashMap();
    protected final ConcurrentHashMapQueue<Integer, CachedEntity> cache = new ConcurrentHashMapQueue();
    protected int cacheTTL = 5000;
    protected final EntityManager manager;
    protected final boolean useEntityComponents;
    public final String entityName;
    protected final Function<T, Integer> uniqueIDGetter;
    protected int nextUniqueID;
    protected final Function<T, Boolean> isRemoved;
    protected final Consumer<T> dispose;
    protected final Consumer<T> onAdded;
    Consumer<T> onHiddenAdded;
    protected final Consumer<T> onRemoved;
    protected final EntityRegionList<T> regionList;

    public EntityList(EntityManager manager, boolean useEntityComponents, String entityName, Function<T, Integer> uniqueIDGetter, Function<T, Boolean> isRemoved, Consumer<T> dispose, Consumer<T> onAdded, Consumer<T> onRemoved, boolean trackRegions) {
        this.manager = manager;
        this.useEntityComponents = useEntityComponents;
        this.entityName = entityName;
        this.uniqueIDGetter = uniqueIDGetter;
        this.isRemoved = isRemoved;
        this.dispose = dispose;
        this.onAdded = onAdded;
        this.onRemoved = onRemoved;
        this.regionList = trackRegions ? new EntityRegionList(this.getLevel()) : null;
    }

    public EntityList(EntityManager manager, boolean useEntityComponents, String entityName, Consumer<T> onAdded, Consumer<T> onRemoved, boolean trackRegions) {
        this(manager, useEntityComponents, entityName, Entity::getUniqueID, Entity::removed, e -> {
            if (!e.isDisposed()) {
                e.dispose();
            }
        }, onAdded, onRemoved, trackRegions);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasUniqueID(int uniqueID, boolean searchCache) {
        if (this.uniqueIDGetter == null) {
            return false;
        }
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
    public T get(int uniqueID, boolean searchCache) {
        Object out;
        if (this.uniqueIDGetter == null) {
            return null;
        }
        Object object = this.manager.lock;
        synchronized (object) {
            out = (Entity)this.map.get(uniqueID);
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
        if (this.uniqueIDGetter != null) {
            int uniqueID = this.uniqueIDGetter.apply(entity);
            ((Entity)entity).setUniqueID(uniqueID);
            Object object = this.manager.lock;
            synchronized (object) {
                this.map.compute(uniqueID, (uid, last) -> {
                    if (last != null && last != entity) {
                        this.runRemoveLogic(last);
                    }
                    return entity;
                });
                if (this.useEntityComponents) {
                    this.manager.componentManager.add(uniqueID, entity);
                    if (entity instanceof LevelBuffsEntityComponent) {
                        this.manager.level.buffManager.updateBuffs();
                    }
                }
            }
        }
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.map.containsValue(entity)) {
                return;
            }
            int uniqueID = this.nextUniqueID++;
            ((Entity)entity).setUniqueID(uniqueID);
            this.map.compute(uniqueID, (uid, last) -> {
                if (last != null && last != entity) {
                    this.runRemoveLogic(last);
                }
                return entity;
            });
            if (this.useEntityComponents) {
                this.manager.componentManager.add(uniqueID, entity);
                if (entity instanceof LevelBuffsEntityComponent) {
                    this.getLevel().buffManager.updateBuffs();
                }
            }
        }
        ((Entity)entity).setLevel(this.getLevel());
        if (!((Entity)entity).isInitialized()) {
            ((Entity)entity).init();
            ((Entity)entity).postInit();
        } else {
            ((Entity)entity).onLevelChanged();
        }
        if (this.onHiddenAdded != null) {
            this.onHiddenAdded.accept(entity);
        }
        if (this.regionList != null) {
            Object object2 = this.manager.lock;
            synchronized (object2) {
                this.regionList.updateRegion(entity);
            }
        }
    }

    public EntityRegionList<T> getRegionList() {
        return this.regionList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<T> getSaveToRegion(int regionX, int regionY) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.getSaveToRegion(regionX, regionY);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<T> getInRegion(int regionX, int regionY) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.getInRegion(regionX, regionY);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<T> getInNoRegion() {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.getInNoRegion();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream<T> streamInRegionsShape(Shape shape, int extraRegionRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.streamInRegionsShape(shape, extraRegionRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream<T> streamInRegionsInRange(float x, float y, int range) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.streamInRegionsInRange(x, y, range);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream<T> streamInRegionsInTileRange(int x, int y, int tileRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.streamInRegionsInTileRange(x, y, tileRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameAreaStream<T> streamArea(float x, float y, int range) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.streamArea(x, y, range);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameAreaStream<T> streamAreaTileRange(int x, int y, int tileRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.streamAreaTileRange(x, y, tileRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<T> getInRegionTileByTile(int tileX, int tileY) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.getInRegionTileByTile(tileX, tileY);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<T> getInRegionRange(int regionX, int regionY, int regionRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.getInRegionRange(regionX, regionY, regionRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<T> getInRegionRangeByTile(int tileX, int tileY, int regionRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
            return this.regionList.getInRegionRangeByTile(tileX, tileY, regionRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<T> getInRegionByTileRange(int tileX, int tileY, int tileRange) {
        Object object = this.manager.lock;
        synchronized (object) {
            if (this.regionList == null) {
                throw new IllegalStateException(this.entityName + " list region data not supported");
            }
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
                    for (Entity entity : this.map.values()) {
                        if (entity == null || this.isRemoved.apply(entity).booleanValue()) continue;
                        tickMovement.accept(entity, Float.valueOf(delta));
                    }
                }
            }));
        }
        if (this.regionList != null) {
            Performance.record((PerformanceTimerManager)level.tickManager(), "regionEnt", () -> Performance.record((PerformanceTimerManager)level.tickManager(), this.entityName, () -> {
                Object object = this.manager.lock;
                synchronized (object) {
                    for (Entity entity : this.map.values()) {
                        if (entity == null) continue;
                        this.regionList.updateRegion(entity);
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
                } else if (this.isRemoved.apply((Entity)entity).booleanValue()) {
                    removes.add(uniqueID);
                    this.runRemoveLogic(entity);
                    if (this.cacheTTL > 0) {
                        this.cache.addLast((Integer)uniqueID, new CachedEntity(this, uniqueID.intValue(), entity));
                    }
                } else if (entity.getUniqueID() != uniqueID.intValue()) {
                    GameLog.warn.println(this.entityName + " has changed uniqueID from " + uniqueID + " to " + entity.getUniqueID() + ", removing it from level");
                    removes.add(uniqueID);
                    this.runRemoveLogic(entity);
                } else {
                    if (entity.shouldDrawOnMap()) {
                        drawOnMap.add((DrawOnMapEntity)entity);
                    }
                    entityTick.accept(entity);
                    entity.updateRegionPos();
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                int uniqueID2 = (Integer)iterator.next();
                Entity remove = (Entity)this.map.remove(uniqueID2);
                if (!this.useEntityComponents || remove == null) continue;
                this.manager.componentManager.remove(uniqueID2, remove);
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
                } else if (this.isRemoved.apply((Entity)entity).booleanValue()) {
                    removes.add(uniqueID);
                    this.runRemoveLogic(entity);
                    if (this.cacheTTL > 0) {
                        this.cache.addLast((Integer)uniqueID, new CachedEntity(this, uniqueID.intValue(), entity));
                    }
                } else if (entity.getUniqueID() != uniqueID.intValue()) {
                    GameLog.warn.println(this.entityName + " has changed uniqueID from " + uniqueID + " to " + entity.getUniqueID() + ", removing it from level");
                    removes.add(uniqueID);
                    this.runRemoveLogic(entity);
                } else {
                    if (entity.shouldDrawOnMap()) {
                        drawOnMap.add((DrawOnMapEntity)entity);
                    }
                    entityTick.accept(entity);
                    entity.updateRegionPos();
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                int uniqueID2 = (Integer)iterator.next();
                Entity remove = (Entity)this.map.remove(uniqueID2);
                if (!this.useEntityComponents || remove == null) continue;
                this.manager.componentManager.remove(uniqueID2, remove);
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
        if (!((Entity)entity).removed()) {
            ((Entity)entity).remove();
        }
        ((Entity)entity).onRemovedFromManager();
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
            this.map.values().forEach(Entity::onLoadingComplete);
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

    protected static class CachedEntity {
        public final int uniqueID;
        public final T entity;
        public final long endTime;
        final /* synthetic */ EntityList this$0;

        public CachedEntity(int uniqueID, T entity) {
            this.this$0 = this$0;
            this.uniqueID = uniqueID;
            this.entity = entity;
            this.endTime = this$0.getLevel().getTime() + (long)this$0.cacheTTL;
        }

        public boolean shouldDie() {
            return this.endTime <= this.this$0.getLevel().getTime();
        }
    }
}

