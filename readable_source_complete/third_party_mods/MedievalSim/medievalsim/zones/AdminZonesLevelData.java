/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.network.packet.PacketPlaceObject
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.PointHashSet
 *  necesse.engine.util.PointTreeSet
 *  necesse.engine.util.Zoning
 *  necesse.entity.manager.RegionLoadedListenerEntityComponent
 *  necesse.level.maps.Level
 *  necesse.level.maps.levelData.LevelData
 *  necesse.level.maps.regionSystem.Region
 */
package medievalsim.zones;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import medievalsim.packets.PacketZoneChanged;
import medievalsim.packets.PacketZoneRemoved;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZone;
import medievalsim.zones.BarrierPlacementWorker;
import medievalsim.zones.ProtectedZone;
import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneBarrierManager;
import medievalsim.zones.PvPZoneDotHandler;
import medievalsim.zones.ZoneConstants;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointTreeSet;
import necesse.engine.util.Zoning;
import necesse.entity.manager.RegionLoadedListenerEntityComponent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.regionSystem.Region;

public class AdminZonesLevelData
extends LevelData
implements RegionLoadedListenerEntityComponent {
    private final Map<Integer, ProtectedZone> protectedZones = new HashMap<Integer, ProtectedZone>();
    private final Map<Integer, PvPZone> pvpZones = new HashMap<Integer, PvPZone>();
    private final AtomicInteger nextUniqueID = new AtomicInteger(1);
    private boolean hasCreatedInitialBarriers = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ProtectedZone getProtectedZone(int uniqueID) {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            return this.protectedZones.get(uniqueID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PvPZone getPvPZone(int uniqueID) {
        Map<Integer, PvPZone> map;
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            return this.pvpZones.get(uniqueID);
        }
    }

    public AdminZone getZone(int uniqueID) {
        ProtectedZone zone = this.getProtectedZone(uniqueID);
        if (zone != null) {
            return zone;
        }
        return this.getPvPZone(uniqueID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<Integer, ProtectedZone> getProtectedZones() {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            return new HashMap<Integer, ProtectedZone>(this.protectedZones);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<Integer, PvPZone> getPvPZones() {
        Map<Integer, PvPZone> map;
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            return new HashMap<Integer, PvPZone>(this.pvpZones);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forEachProtectedZone(Consumer<ProtectedZone> action) {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            this.protectedZones.values().forEach(action);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forEachPvPZone(Consumer<PvPZone> action) {
        Map<Integer, PvPZone> map;
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            this.pvpZones.values().forEach(action);
        }
    }

    public Map<Integer, ProtectedZone> getProtectedZonesInternal() {
        return this.protectedZones;
    }

    public Map<Integer, PvPZone> getPvPZonesInternal() {
        return this.pvpZones;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ProtectedZone addProtectedZone(String name, long creatorAuth, int colorHue) {
        Map<Integer, ProtectedZone> map;
        int uniqueID = this.nextUniqueID.getAndIncrement();
        ProtectedZone zone = new ProtectedZone(uniqueID, name, creatorAuth, colorHue);
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            this.protectedZones.put(uniqueID, zone);
        }
        return zone;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putProtectedZone(ProtectedZone zone) {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            this.protectedZones.put(zone.uniqueID, zone);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PvPZone addPvPZone(String name, long creatorAuth, int colorHue) {
        Map<Integer, PvPZone> map;
        int uniqueID = this.nextUniqueID.getAndIncrement();
        PvPZone zone = new PvPZone(uniqueID, name, creatorAuth, colorHue);
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            this.pvpZones.put(uniqueID, zone);
        }
        return zone;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putPvPZone(PvPZone zone) {
        Map<Integer, PvPZone> map;
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            this.pvpZones.put(zone.uniqueID, zone);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeProtectedZone(int uniqueID) {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            ProtectedZone zone = this.protectedZones.get(uniqueID);
            if (zone != null) {
                zone.remove();
                this.protectedZones.remove(uniqueID);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePvPZone(int uniqueID) {
        Map<Integer, PvPZone> map;
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            PvPZone zone = this.pvpZones.get(uniqueID);
            if (zone != null) {
                try {
                    zone.removeBarriers(this.level);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                BarrierPlacementWorker.removeQueuedTasksForZone(this.level, uniqueID);
                zone.remove();
                this.pvpZones.remove(uniqueID);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearProtectedZones() {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            this.protectedZones.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearPvPZones() {
        Map<Integer, PvPZone> map;
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            this.pvpZones.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ProtectedZone getProtectedZoneAt(int tileX, int tileY) {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> map2 = map = this.protectedZones;
        synchronized (map2) {
            for (ProtectedZone zone : this.protectedZones.values()) {
                Rectangle bounds = zone.zoning.getTileBounds();
                if (bounds == null || !bounds.contains(tileX, tileY) || !zone.containsTile(tileX, tileY)) continue;
                return zone;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PvPZone getPvPZoneAt(float x, float y) {
        Map<Integer, PvPZone> map;
        int tileX = GameMath.getTileCoordinate((int)((int)x));
        int tileY = GameMath.getTileCoordinate((int)((int)y));
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            for (PvPZone zone : this.pvpZones.values()) {
                Rectangle bounds = zone.zoning.getTileBounds();
                if (bounds == null || !bounds.contains(tileX, tileY) || !zone.containsTile(tileX, tileY)) continue;
                return zone;
            }
        }
        return null;
    }

    public boolean canClientModifyTile(ServerClient client, int tileX, int tileY) {
        ProtectedZone zone = this.getProtectedZoneAt(tileX, tileY);
        if (zone == null) {
            return true;
        }
        return zone.canClientModify(client, this.level);
    }

    public boolean areBothInPvPZone(float x1, float y1, float x2, float y2) {
        PvPZone zone1 = this.getPvPZoneAt(x1, y1);
        PvPZone zone2 = this.getPvPZoneAt(x2, y2);
        return zone1 != null && zone2 != null && zone1 == zone2;
    }

    public PvPZone getPvPZoneContainingBoth(float x1, float y1, float x2, float y2) {
        PvPZone zone1 = this.getPvPZoneAt(x1, y1);
        PvPZone zone2 = this.getPvPZoneAt(x2, y2);
        if (zone1 != null && zone2 != null && zone1 == zone2) {
            return zone1;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        super.tick();
        if (this.level != null && this.level.isServer() && !this.hasCreatedInitialBarriers) {
            this.hasCreatedInitialBarriers = true;
            ArrayList<PvPZone> snapshot = new ArrayList<PvPZone>();
            Map<Integer, PvPZone> map = this.pvpZones;
            Map<Integer, PvPZone> map2 = map;
            synchronized (map2) {
                snapshot.addAll(this.pvpZones.values());
            }
            for (PvPZone zone : snapshot) {
                try {
                    zone.createBarriers(this.level);
                }
                catch (Exception e) {
                    ModLogger.error("Error creating initial barriers for zone '" + (zone != null ? zone.name : "<null>") + "'", e);
                }
            }
        }
        BarrierPlacementWorker.processTick(this.level, ZoneConstants.getBarrierMaxTilesPerTick());
        try {
            if (this.level != null && this.level.isServer()) {
                PvPZoneDotHandler.processLevelTick(this.level);
            }
        }
        catch (Throwable t) {
            ModLogger.error("Error running PvPZoneDotHandler", t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onRegionLoaded(Region region) {
        Map<Integer, PvPZone> map;
        if (region == null || this.level == null || !this.level.isServer()) {
            return;
        }
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            for (PvPZone zone : this.pvpZones.values()) {
                try {
                    PointHashSet edge = zone.zoning.getEdgeTiles();
                    if (edge == null || edge.isEmpty()) continue;
                    boolean intersects = false;
                    for (Object o : edge) {
                        if (!(o instanceof Point)) continue;
                        Point p = (Point)o;
                        int rX = this.level.regionManager.getRegionXByTileLimited(p.x);
                        int rY = this.level.regionManager.getRegionYByTileLimited(p.y);
                        if (rX != region.regionX || rY != region.regionY) continue;
                        intersects = true;
                        break;
                    }
                    if (!intersects) continue;
                    BarrierPlacementWorker.queueZoneRegionPlacement(this.level, zone, region);
                }
                catch (Exception e) {
                    ModLogger.error("Error queuing barrier placement on region load", e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addSaveData(SaveData save) {
        Map<Integer, PvPZone> map2;
        Map<Integer, ProtectedZone> map;
        super.addSaveData(save);
        ModLogger.info("Saving AdminZonesLevelData - protected=%d pvp=%d nextID=%d", this.protectedZones.size(), this.pvpZones.size(), this.nextUniqueID.get());
        save.addInt("nextUniqueID", this.nextUniqueID.get());
        save.addBoolean("hasCreatedInitialBarriers", this.hasCreatedInitialBarriers);
        SaveData protectedSave = new SaveData("PROTECTED_ZONES");
        Map<Integer, ProtectedZone> map3 = map = this.protectedZones;
        synchronized (map3) {
            for (ProtectedZone zone : this.protectedZones.values()) {
                if (zone.shouldRemove()) continue;
                SaveData zoneSave = new SaveData("ZONE");
                zone.addSaveData(zoneSave);
                protectedSave.addSaveData(zoneSave);
            }
        }
        save.addSaveData(protectedSave);
        SaveData pvpSave = new SaveData("PVP_ZONES");
        Map<Integer, PvPZone> map4 = map2 = this.pvpZones;
        synchronized (map4) {
            for (PvPZone zone : this.pvpZones.values()) {
                if (zone.shouldRemove()) continue;
                SaveData zoneSave = new SaveData("ZONE");
                zone.addSaveData(zoneSave);
                pvpSave.addSaveData(zoneSave);
            }
        }
        save.addSaveData(pvpSave);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyLoadData(LoadData save) {
        LoadData pvpSave;
        Map<Integer, ProtectedZone> map;
        Map<Integer, AdminZone> map2;
        super.applyLoadData(save);
        ModLogger.info("Loading AdminZonesLevelData - nextUniqueID(before)=%d", this.nextUniqueID.get());
        this.nextUniqueID.set(save.getInt("nextUniqueID", 1));
        this.hasCreatedInitialBarriers = save.getBoolean("hasCreatedInitialBarriers", false);
        LoadData protectedSave = save.getFirstLoadDataByName("PROTECTED_ZONES");
        if (protectedSave != null) {
            map = map2 = this.protectedZones;
            synchronized (map) {
                this.protectedZones.clear();
                for (LoadData zoneSave : protectedSave.getLoadDataByName("ZONE")) {
                    ProtectedZone zone = new ProtectedZone();
                    zone.applyLoadData(zoneSave);
                    this.protectedZones.put(zone.uniqueID, zone);
                }
            }
        }
        if ((pvpSave = save.getFirstLoadDataByName("PVP_ZONES")) != null) {
            map = map2 = this.pvpZones;
            synchronized (map) {
                this.pvpZones.clear();
                for (LoadData zoneSave : pvpSave.getLoadDataByName("ZONE")) {
                    PvPZone pvPZone = new PvPZone();
                    pvPZone.applyLoadData(zoneSave);
                    this.pvpZones.put(pvPZone.uniqueID, pvPZone);
                }
            }
        }
        ModLogger.info("Loaded AdminZonesLevelData - protected=%d pvp=%d nextID=%d", this.protectedZones.size(), this.pvpZones.size(), this.nextUniqueID.get());
        int maxID = 0;
        Map<Integer, ProtectedZone> map3 = this.protectedZones;
        Map<Integer, ProtectedZone> map4 = map3;
        synchronized (map4) {
            for (ProtectedZone protectedZone : this.protectedZones.values()) {
                maxID = Math.max(maxID, protectedZone.uniqueID);
            }
        }
        Map<Integer, PvPZone> map22 = this.pvpZones;
        Map<Integer, PvPZone> map5 = map22;
        synchronized (map5) {
            for (PvPZone pvPZone : this.pvpZones.values()) {
                maxID = Math.max(maxID, pvPZone.uniqueID);
            }
        }
        if (this.nextUniqueID.get() <= maxID) {
            this.nextUniqueID.set(maxID + 1);
            ModLogger.info("Corrected nextUniqueID from %d to %d", save.getInt("nextUniqueID", 1), maxID + 1);
        }
    }

    public static AdminZonesLevelData getAdminZonesData(Level level) {
        return AdminZonesLevelData.getZoneData(level, false);
    }

    public static AdminZonesLevelData getZoneData(Level level) {
        return AdminZonesLevelData.getZoneData(level, true);
    }

    public static AdminZonesLevelData getZoneData(Level level, boolean createIfNull) {
        if (level == null) {
            return null;
        }
        LevelData data = level.getLevelData("adminzonesdata");
        if (data instanceof AdminZonesLevelData) {
            return (AdminZonesLevelData)data;
        }
        if (createIfNull) {
            AdminZonesLevelData newData = new AdminZonesLevelData();
            level.addLevelData("adminzonesdata", (LevelData)newData);
            return newData;
        }
        return null;
    }

    public List<AdminZone> splitZoneIfDisconnected(AdminZone zone, Level level) {
        int i;
        ArrayList<AdminZone> affected = new ArrayList<AdminZone>();
        if (zone == null || level == null) {
            return affected;
        }
        PointTreeSet tiles = zone.zoning.getTiles();
        if (tiles == null || tiles.isEmpty()) {
            affected.add(zone);
            return affected;
        }
        HashSet<Point> unvisited = new HashSet<Point>();
        for (Object o : tiles) {
            if (!(o instanceof Point)) continue;
            Point p = (Point)o;
            unvisited.add(new Point(p));
        }
        ArrayList components = new ArrayList();
        int[] dx = new int[]{0, 1, 0, -1};
        int[] dy = new int[]{-1, 0, 1, 0};
        while (!unvisited.isEmpty()) {
            Point start = (Point)unvisited.iterator().next();
            LinkedList<Object> queue = new LinkedList<Object>();
            ArrayList<Point> comp = new ArrayList<Point>();
            queue.add(start);
            unvisited.remove(start);
            while (!queue.isEmpty()) {
                Point cur = (Point)queue.removeFirst();
                comp.add(cur);
                for (int i2 = 0; i2 < 4; ++i2) {
                    Point n = new Point(cur.x + dx[i2], cur.y + dy[i2]);
                    if (!unvisited.remove(n)) continue;
                    queue.add(n);
                }
            }
            components.add(comp);
        }
        if (components.size() <= 1) {
            affected.add(zone);
            return affected;
        }
        int bestIdx = 0;
        int bestSize = 0;
        for (i = 0; i < components.size(); ++i) {
            int s = ((List)components.get(i)).size();
            if (s <= bestSize) continue;
            bestSize = s;
            bestIdx = i;
        }
        for (i = 0; i < components.size(); ++i) {
            AdminZone newZone;
            if (i == bestIdx) continue;
            List comp = (List)components.get(i);
            if (zone instanceof PvPZone) {
                newZone = this.addPvPZone(this.getUniqueZoneName(), zone.creatorAuth, zone.colorHue);
                for (Point p : comp) {
                    newZone.zoning.addTile(p.x, p.y);
                }
                this.putPvPZone((PvPZone)newZone);
                affected.add(newZone);
                continue;
            }
            if (!(zone instanceof ProtectedZone)) continue;
            newZone = this.addProtectedZone(this.getUniqueZoneName(), zone.creatorAuth, zone.colorHue);
            for (Point p : comp) {
                ((ProtectedZone)newZone).zoning.addTile(p.x, p.y);
            }
            this.putProtectedZone((ProtectedZone)newZone);
            affected.add(newZone);
        }
        List mainComp = (List)components.get(bestIdx);
        Zoning newZoning = new Zoning(true);
        for (Point p : mainComp) {
            newZoning.addTile(p.x, p.y);
        }
        zone.zoning = newZoning;
        affected.add(zone);
        ModLogger.info("Split zone '%s' into %d parts", zone.name, components.size());
        return affected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<AdminZone> resolveAfterZoneChange(AdminZone targetZone, Level level, Server server, boolean isProtectedZone, Map<Integer, Collection<Point>> oldEdgesByZoneID) {
        AdminZone pz;
        Map<Integer, AdminZone> p22;
        Map<Integer, AdminZone> map;
        Object object;
        ArrayList<AdminZone> result = new ArrayList<AdminZone>();
        if (targetZone == null || level == null) {
            return result;
        }
        HashSet<AdminZone> candidates = new HashSet<AdminZone>();
        if (isProtectedZone) {
            Map<Integer, ProtectedZone> map2 = this.protectedZones;
            object = map2;
            synchronized (object) {
                for (ProtectedZone protectedZone : this.protectedZones.values()) {
                    if (protectedZone == targetZone) {
                        candidates.add(protectedZone);
                        continue;
                    }
                    if (!this.areZonesAdjacentOrOverlapping(targetZone, protectedZone)) continue;
                    candidates.add(protectedZone);
                }
            }
        }
        Map<Integer, PvPZone> map2 = this.pvpZones;
        object = map2;
        synchronized (object) {
            for (PvPZone pvPZone : this.pvpZones.values()) {
                if (pvPZone == targetZone) {
                    candidates.add(pvPZone);
                    continue;
                }
                if (!this.areZonesAdjacentOrOverlapping(targetZone, pvPZone)) continue;
                candidates.add(pvPZone);
            }
        }
        if (candidates.size() <= 1) {
            List<AdminZone> affected = this.splitZoneIfDisconnected(targetZone, level);
            for (AdminZone az : affected) {
                if (az instanceof PvPZone) {
                    Collection<Point> collection;
                    Collection<Point> collection2 = collection = oldEdgesByZoneID != null ? oldEdgesByZoneID.get(az.uniqueID) : null;
                    if (collection != null) {
                        PvPZoneBarrierManager.updateBarrier(level, (PvPZone)az, collection);
                    } else {
                        PointHashSet pointHashSet = az.zoning.getEdgeTiles();
                        if (pointHashSet != null) {
                            for (Object o : pointHashSet) {
                                if (!(o instanceof Point)) continue;
                                Point p22 = (Point)o;
                                Region region = level.regionManager.getRegionByTile(p22.x, p22.y, false);
                                if (region == null) continue;
                                BarrierPlacementWorker.queueZoneRegionPlacement(level, (PvPZone)az, region);
                            }
                        }
                    }
                }
                if (server != null) {
                    server.network.sendToAllClients((Packet)new PacketZoneChanged(az, isProtectedZone));
                }
                result.add(az);
            }
            return result;
        }
        HashSet<AdminZone> mergeSet = new HashSet<AdminZone>(candidates);
        mergeSet.add(targetZone);
        AdminZone winner = null;
        int bestSize = -1;
        for (AdminZone adminZone : mergeSet) {
            int sz = adminZone.zoning.getTiles() != null ? adminZone.zoning.getTiles().size() : 0;
            if (sz <= bestSize) continue;
            bestSize = sz;
            winner = adminZone;
        }
        if (winner == null) {
            return result;
        }
        HashSet<Point> hashSet = new HashSet<Point>();
        for (AdminZone z : mergeSet) {
            PointTreeSet t = z.zoning.getTiles();
            if (t == null) continue;
            for (Object o : t) {
                if (!(o instanceof Point)) continue;
                Point p3 = (Point)o;
                hashSet.add(new Point(p3));
            }
        }
        Zoning zoning = new Zoning(true);
        for (Point p4 : hashSet) {
            zoning.addTile(p4.x, p4.y);
        }
        ArrayList<AdminZone> removed = new ArrayList<AdminZone>();
        if (isProtectedZone) {
            p22 = map = this.protectedZones;
            synchronized (p22) {
                for (AdminZone z : new ArrayList(mergeSet)) {
                    if (z == winner || !(z instanceof ProtectedZone)) continue;
                    pz = (ProtectedZone)z;
                    pz.remove();
                    this.protectedZones.remove(((ProtectedZone)pz).uniqueID);
                    removed.add(pz);
                }
            }
        }
        p22 = map = this.pvpZones;
        synchronized (p22) {
            for (AdminZone z : new ArrayList(mergeSet)) {
                if (z == winner || !(z instanceof PvPZone)) continue;
                pz = (PvPZone)z;
                try {
                    ((PvPZone)pz).removeBarriers(level);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                BarrierPlacementWorker.removeQueuedTasksForZone(level, ((PvPZone)pz).uniqueID);
                this.pvpZones.remove(((PvPZone)pz).uniqueID);
                removed.add(pz);
            }
        }
        winner.zoning = zoning;
        if (isProtectedZone) {
            this.putProtectedZone((ProtectedZone)winner);
        } else {
            this.putPvPZone((PvPZone)winner);
        }
        List<AdminZone> affected = this.splitZoneIfDisconnected(winner, level);
        ArrayList<Point> combinedOld = null;
        if (oldEdgesByZoneID != null) {
            combinedOld = new ArrayList<Point>();
            for (AdminZone z : mergeSet) {
                Collection<Point> col = oldEdgesByZoneID.get(z.uniqueID);
                if (col == null) continue;
                combinedOld.addAll(col);
            }
        }
        for (AdminZone az : affected) {
            if (az instanceof PvPZone) {
                if (az == winner && combinedOld != null) {
                    PvPZoneBarrierManager.updateBarrier(level, (PvPZone)az, combinedOld);
                } else {
                    PointHashSet edge = az.zoning.getEdgeTiles();
                    if (edge != null) {
                        for (Object o : edge) {
                            if (!(o instanceof Point)) continue;
                            Point p5 = (Point)o;
                            Region region = level.regionManager.getRegionByTile(p5.x, p5.y, false);
                            if (region == null) continue;
                            BarrierPlacementWorker.queueZoneRegionPlacement(level, (PvPZone)az, region);
                        }
                    }
                }
            }
            if (server != null) {
                server.network.sendToAllClients((Packet)new PacketZoneChanged(az, isProtectedZone));
            }
            result.add(az);
        }
        for (AdminZone rem : removed) {
            if (server == null) continue;
            server.network.sendToAllClients((Packet)new PacketZoneRemoved(rem.uniqueID, isProtectedZone));
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceCleanAround(Level level, int centerTileX, int centerTileY, int radius, Server server) {
        Map<Integer, PvPZone> map;
        if (level == null || !level.isServer() || server == null) {
            return;
        }
        int barrierID = PvPZoneBarrierManager.getBarrierObjectID();
        if (barrierID == -1) {
            return;
        }
        int maxX = centerTileX + radius;
        int minX = centerTileX - radius;
        int maxY = centerTileY + radius;
        int minY = centerTileY - radius;
        long area = (long)(maxX - minX + 1) * (long)(maxY - minY + 1);
        long maxArea = Math.max(1000, ZoneConstants.getMaxBarrierTiles() * 4);
        if (area > maxArea) {
            ModLogger.info("forceClean area too large (%d), skipping", area);
            return;
        }
        int removed = 0;
        Map<Integer, PvPZone> map2 = map = this.pvpZones;
        synchronized (map2) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    try {
                        int existing = level.getObjectID(0, x, y);
                        if (existing != barrierID) continue;
                        boolean isEdge = false;
                        for (PvPZone zone : this.pvpZones.values()) {
                            PointHashSet edge = zone.zoning.getEdgeTiles();
                            if (edge == null || !edge.contains(x, y)) continue;
                            isEdge = true;
                            break;
                        }
                        if (isEdge) continue;
                        level.setObject(x, y, 0);
                        server.network.sendToClientsWithTile((Packet)new PacketPlaceObject(level, null, 0, x, y, 0, 0, false, false), level, x, y);
                        ++removed;
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
        ModLogger.info("forceClean removed %d stray barriers around (%d,%d) radius %d", removed, centerTileX, centerTileY, radius);
    }

    private boolean areZonesAdjacentOrOverlapping(AdminZone a, AdminZone b) {
        if (a == null || b == null) {
            return false;
        }
        PointTreeSet ta = a.zoning.getTiles();
        PointTreeSet tb = b.zoning.getTiles();
        if (ta == null || ta.isEmpty() || tb == null || tb.isEmpty()) {
            return false;
        }
        Rectangle ra = a.zoning.getTileBounds();
        Rectangle rb = b.zoning.getTileBounds();
        if (ra == null || rb == null) {
            return false;
        }
        Rectangle expanded = new Rectangle(ra.x - 1, ra.y - 1, ra.width + 2, ra.height + 2);
        if (!expanded.intersects(rb)) {
            return false;
        }
        for (Object oa : ta) {
            if (!(oa instanceof Point)) continue;
            Point pa = (Point)oa;
            for (Object ob : tb) {
                int dy;
                if (!(ob instanceof Point)) continue;
                Point pb = (Point)ob;
                int dx = Math.abs(pa.x - pb.x);
                if (dx + (dy = Math.abs(pa.y - pb.y)) > 1) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getUniqueZoneName() {
        Map<Integer, ProtectedZone> pmap;
        String base = "New Zone";
        int idx = 1;
        HashSet<String> names = new HashSet<String>();
        Map<Integer, ProtectedZone> map = pmap = this.protectedZones;
        synchronized (map) {
            for (ProtectedZone z : this.protectedZones.values()) {
                names.add(z.name);
            }
        }
        Map<Integer, PvPZone> pvmap = this.pvpZones;
        Map<Integer, PvPZone> map2 = pvmap;
        synchronized (map2) {
            for (PvPZone z : this.pvpZones.values()) {
                names.add(z.name);
            }
        }
        while (names.contains(base + " " + idx)) {
            ++idx;
        }
        return base + " " + idx;
    }
}

