/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.util.ConcurrentHashMapQueue;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.LevelEventRegionList;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;

public class LevelEventsManager
implements Iterable<LevelEvent> {
    protected final Level level;
    protected final EntityManager manager;
    protected final ConcurrentHashMap<Integer, LevelEvent> events = new ConcurrentHashMap();
    public int cacheTTL = 5000;
    protected final ConcurrentHashMapQueue<Integer, CachedLevelEvent> eventsCache = new ConcurrentHashMapQueue();
    public final LevelEventRegionList<LevelEvent> regionList;

    public LevelEventsManager(EntityManager manager) {
        this.manager = manager;
        this.level = manager.level;
        this.regionList = new LevelEventRegionList(this.level);
    }

    public void add(LevelEvent event) {
        this.addHidden(event);
        if (this.level.isServer()) {
            this.level.getServer().network.sendToClientsWithEntity(new PacketLevelEvent(event), event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addHidden(LevelEvent event) {
        event.level = this.level;
        int uniqueID = event.getUniqueID();
        event.setUniqueID(uniqueID);
        Object object = this.manager.lock;
        synchronized (object) {
            this.events.compute(uniqueID, (uid, last) -> {
                if (last != null && last != event) {
                    this.runRemoveLogic((LevelEvent)last, false);
                }
                return event;
            });
            this.manager.componentManager.add(uniqueID, event);
            if (event instanceof LevelBuffsEntityComponent) {
                this.level.buffManager.updateBuffs();
            }
        }
        event.init();
        object = this.manager.lock;
        synchronized (object) {
            this.regionList.updateRegion(event);
        }
        if (this.level.isServer() && event instanceof MobAbilityLevelEvent) {
            this.manager.submittedHits.submitNewMobAbilityLevelEvent((MobAbilityLevelEvent)event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LevelEvent get(int uniqueID, boolean searchCache) {
        Object object = this.manager.lock;
        synchronized (object) {
            LevelEvent event = this.events.get(uniqueID);
            if (event != null) {
                return event;
            }
        }
        if (searchCache) {
            object = this.manager.lock;
            synchronized (object) {
                CachedLevelEvent out = (CachedLevelEvent)this.eventsCache.get(uniqueID);
                if (out != null) {
                    return out.event;
                }
            }
        }
        return null;
    }

    public boolean hasUniqueID(int uniqueID) {
        return this.events.containsKey(uniqueID);
    }

    protected void runRemoveLogic(LevelEvent event, boolean sendPacket) {
        event.onDispose();
        if (this.level.isServer() && sendPacket && event.shouldSendOverPacket()) {
            this.level.getServer().network.sendToClientsWithEntity(new PacketLevelEventOver(event.getUniqueID()), event);
        }
    }

    @Override
    public Iterator<LevelEvent> iterator() {
        return this.events.values().iterator();
    }

    public Stream<LevelEvent> stream() {
        return this.events.values().stream();
    }

    public int count() {
        return this.events.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int clearLevelEvents(String stringID) {
        Object object = this.manager.lock;
        synchronized (object) {
            LinkedList removes = new LinkedList();
            this.events.forEach((? super K uniqueID, ? super V event) -> {
                if (event == null) {
                    removes.add(uniqueID);
                } else if (event.getStringID().contains(stringID)) {
                    removes.add(uniqueID);
                    event.over();
                    this.runRemoveLogic((LevelEvent)event, true);
                    if (this.cacheTTL > 0) {
                        this.eventsCache.addLast((Integer)uniqueID, new CachedLevelEvent((int)uniqueID, (LevelEvent)event));
                    }
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                int uniqueID2 = (Integer)iterator.next();
                LevelEvent remove = this.events.remove(uniqueID2);
                if (remove == null) continue;
                this.manager.componentManager.remove(uniqueID2, remove);
                if (!(remove instanceof LevelBuffsEntityComponent)) continue;
                updateBuffs = true;
            }
            if (updateBuffs) {
                this.level.buffManager.updateBuffs();
            }
            return removes.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int clearLevelEvents() {
        Object object = this.manager.lock;
        synchronized (object) {
            int amount = this.events.size();
            LinkedList removes = new LinkedList();
            this.events.forEach((? super K uniqueID, ? super V event) -> {
                removes.add(uniqueID);
                event.over();
                this.runRemoveLogic((LevelEvent)event, true);
                if (this.cacheTTL > 0) {
                    this.eventsCache.addLast((Integer)uniqueID, new CachedLevelEvent((int)uniqueID, (LevelEvent)event));
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                int uniqueID2 = (Integer)iterator.next();
                LevelEvent remove = this.events.remove(uniqueID2);
                if (remove == null) continue;
                this.manager.componentManager.remove(uniqueID2, remove);
                if (!(remove instanceof LevelBuffsEntityComponent)) continue;
                updateBuffs = true;
            }
            if (updateBuffs) {
                this.level.buffManager.updateBuffs();
            }
            return amount;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clientTick() {
        Object object = this.manager.lock;
        synchronized (object) {
            CachedLevelEvent ce;
            while (!this.eventsCache.isEmpty() && (ce = (CachedLevelEvent)this.eventsCache.getFirst()).shouldDie()) {
                this.eventsCache.removeFirst();
            }
            LinkedList removes = new LinkedList();
            this.events.forEach((? super K uniqueID, ? super V event) -> {
                if (event == null) {
                    removes.add(uniqueID);
                } else if (event.isOver()) {
                    removes.add(uniqueID);
                    this.runRemoveLogic((LevelEvent)event, true);
                    if (this.cacheTTL > 0) {
                        this.eventsCache.addLast((Integer)uniqueID, new CachedLevelEvent((int)uniqueID, (LevelEvent)event));
                    }
                } else if (event.getUniqueID() != uniqueID.intValue()) {
                    GameLog.warn.println("LevelEvent has changed uniqueID from " + uniqueID + " to " + event.getUniqueID() + ", removing it from level");
                    removes.add(uniqueID);
                    this.runRemoveLogic((LevelEvent)event, true);
                } else {
                    event.clientTick();
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                int uniqueID2 = (Integer)iterator.next();
                LevelEvent remove = this.events.remove(uniqueID2);
                if (remove == null) continue;
                this.manager.componentManager.remove(uniqueID2, remove);
                if (!(remove instanceof LevelBuffsEntityComponent)) continue;
                updateBuffs = true;
            }
            if (updateBuffs) {
                this.level.buffManager.updateBuffs();
            }
        }
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "regionEnt", () -> Performance.record((PerformanceTimerManager)this.level.tickManager(), "events", () -> {
            Object object = this.manager.lock;
            synchronized (object) {
                this.events.values().forEach(this.regionList::updateRegion);
            }
        }));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serverTick() {
        Object object = this.manager.lock;
        synchronized (object) {
            CachedLevelEvent ce;
            while (!this.eventsCache.isEmpty() && (ce = (CachedLevelEvent)this.eventsCache.getFirst()).shouldDie()) {
                this.eventsCache.removeFirst();
            }
            LinkedList removes = new LinkedList();
            this.events.forEach((? super K uniqueID, ? super V event) -> {
                if (event == null) {
                    removes.add(uniqueID);
                } else if (event.isOver()) {
                    removes.add(uniqueID);
                    this.runRemoveLogic((LevelEvent)event, true);
                    if (this.cacheTTL > 0) {
                        this.eventsCache.addLast((Integer)uniqueID, new CachedLevelEvent((int)uniqueID, (LevelEvent)event));
                    }
                } else if (event.getUniqueID() != uniqueID.intValue()) {
                    GameLog.warn.println("LevelEvent has changed uniqueID from " + uniqueID + " to " + event.getUniqueID() + ", removing it from level");
                    removes.add(uniqueID);
                    this.runRemoveLogic((LevelEvent)event, true);
                } else {
                    event.serverTick();
                }
            });
            boolean updateBuffs = false;
            Iterator iterator = removes.iterator();
            while (iterator.hasNext()) {
                int uniqueID2 = (Integer)iterator.next();
                LevelEvent remove = this.events.remove(uniqueID2);
                if (remove == null) continue;
                this.manager.componentManager.remove(uniqueID2, remove);
                if (!(remove instanceof LevelBuffsEntityComponent)) continue;
                updateBuffs = true;
            }
            if (updateBuffs) {
                this.level.buffManager.updateBuffs();
            }
        }
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "regionEnt", () -> Performance.record((PerformanceTimerManager)this.level.tickManager(), "events", () -> {
            Object object = this.manager.lock;
            synchronized (object) {
                this.events.values().forEach(this.regionList::updateRegion);
            }
        }));
    }

    public void onLoadingComplete() {
        this.events.values().forEach(LevelEvent::onLoadingComplete);
    }

    public void onUnloading() {
        this.events.values().forEach(event -> event.onUnloading(null));
    }

    public void dispose() {
        this.events.values().forEach(LevelEvent::onDispose);
    }

    protected class CachedLevelEvent {
        public final int uniqueID;
        public final LevelEvent event;
        public final long endTime;

        public CachedLevelEvent(int uniqueID, LevelEvent event) {
            this.uniqueID = uniqueID;
            this.event = event;
            this.endTime = LevelEventsManager.this.level.getTime() + (long)LevelEventsManager.this.cacheTTL;
        }

        public boolean shouldDie() {
            return this.endTime <= LevelEventsManager.this.level.getWorldEntity().getTime();
        }
    }
}

