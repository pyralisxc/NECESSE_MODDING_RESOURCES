/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.network.packet.PacketPlaceObject
 *  necesse.engine.util.PointHashSet
 *  necesse.level.maps.Level
 *  necesse.level.maps.regionSystem.Region
 */
package medievalsim.zones;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneBarrierManager;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public final class BarrierPlacementWorker {
    private static final Map<Level, Queue<PlacementTask>> queues = new ConcurrentHashMap<Level, Queue<PlacementTask>>();

    private BarrierPlacementWorker() {
    }

    public static void queueZoneRegionPlacement(Level level, AdminZone zone, Region region) {
        if (level == null || zone == null || region == null) {
            return;
        }
        Queue q = queues.computeIfAbsent(level, k -> new ConcurrentLinkedQueue());
        q.add(new PlacementTask(zone.uniqueID, region.regionX, region.regionY));
        ModLogger.info("Queued barrier placement for zone '%s' region (%d,%d)", zone.name, region.regionX, region.regionY);
    }

    public static void processTick(Level level, int maxTilesPerTick) {
        PlacementTask task;
        if (level == null) {
            return;
        }
        Queue<PlacementTask> q = queues.get(level);
        if (q == null) {
            return;
        }
        int processed = 0;
        while (processed < maxTilesPerTick && (task = q.peek()) != null) {
            int allowed = maxTilesPerTick - processed;
            int did = task.processUpTo(allowed, level);
            processed += did;
            if (task.isComplete()) {
                q.poll();
            }
            if (did != 0) continue;
            break;
        }
        if (processed > 0) {
            ModLogger.debug("Processed %d barrier placements this tick", processed);
        }
    }

    public static void removeQueuedTasksForZone(Level level, int zoneID) {
        if (level == null) {
            return;
        }
        Queue<PlacementTask> q = queues.get(level);
        if (q == null) {
            return;
        }
        try {
            q.removeIf(task -> task.zoneID == zoneID);
        }
        catch (Exception e) {
            ModLogger.error("Failed to remove queued tasks for zone %s", e, zoneID);
        }
    }

    static final class PlacementTask {
        final int zoneID;
        final int regionX;
        final int regionY;
        private Iterator<Point> remainingIterator = null;
        private boolean initialized = false;

        PlacementTask(int zoneID, int regionX, int regionY) {
            this.zoneID = zoneID;
            this.regionX = regionX;
            this.regionY = regionY;
        }

        private void init(Level level) {
            if (this.initialized) {
                return;
            }
            this.initialized = true;
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
            if (zoneData == null) {
                return;
            }
            PvPZone zone = zoneData.getPvPZone(this.zoneID);
            if (zone == null || zone.shouldRemove()) {
                this.remainingIterator = null;
                return;
            }
            PointHashSet edge = zone.zoning.getEdgeTiles();
            ArrayList<Point> points = new ArrayList<Point>();
            if (edge != null) {
                for (Object o : edge) {
                    if (!(o instanceof Point)) continue;
                    Point p = (Point)o;
                    int rX = level.regionManager.getRegionXByTileLimited(p.x);
                    int rY = level.regionManager.getRegionYByTileLimited(p.y);
                    if (rX != this.regionX || rY != this.regionY) continue;
                    points.add(new Point(p.x, p.y));
                }
            }
            this.remainingIterator = points.iterator();
        }

        int processUpTo(int maxTiles, Level level) {
            if (!this.initialized) {
                this.init(level);
            }
            if (this.remainingIterator == null) {
                return 0;
            }
            int did = 0;
            while (did < maxTiles && this.remainingIterator.hasNext()) {
                Point p = this.remainingIterator.next();
                try {
                    PvPZone zone = AdminZonesLevelData.getZoneData(level, false).getPvPZone(this.zoneID);
                    if (zone == null || zone.shouldRemove()) {
                        this.remainingIterator = null;
                        return did;
                    }
                    int barrierID = PvPZoneBarrierManager.getBarrierObjectID();
                    if (barrierID == -1) break;
                    int existing = level.getObjectID(0, p.x, p.y);
                    if (existing != barrierID) {
                        level.setObject(p.x, p.y, barrierID);
                        if (level.getServer() != null) {
                            level.getServer().network.sendToClientsWithTile((Packet)new PacketPlaceObject(level, null, 0, p.x, p.y, barrierID, 0, false, false), level, p.x, p.y);
                        }
                    }
                    ++did;
                }
                catch (Exception e) {
                    ModLogger.error("Failed to place barrier tile", e);
                }
            }
            return did;
        }

        boolean isComplete() {
            return this.remainingIterator == null || !this.remainingIterator.hasNext();
        }
    }
}

