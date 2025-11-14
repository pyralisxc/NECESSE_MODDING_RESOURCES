/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.network.packet.PacketPlaceObject
 *  necesse.engine.network.packet.PacketPlayerMovement
 *  necesse.engine.network.packet.PacketPlayerPvP
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.util.PointHashSet
 *  necesse.engine.util.Zoning
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.level.maps.Level
 *  necesse.level.maps.regionSystem.RegionPositionGetter
 */
package medievalsim.zones;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import medievalsim.util.ModLogger;
import medievalsim.util.RuntimeConstants;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneTracker;
import medievalsim.zones.ZoneConstants;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class PvPZoneBarrierManager {
    private static int barrierObjectID = -1;

    public static int getBarrierObjectID() {
        if (barrierObjectID == -1) {
            barrierObjectID = ObjectRegistry.getObjectID((String)"pvpzonebarrier");
        }
        return barrierObjectID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void createBarrier(Level level, PvPZone zone) {
        PointHashSet edgeTiles;
        Zoning zoning;
        if (!level.isServer()) {
            return;
        }
        Server server = level.getServer();
        if (server == null) {
            ModLogger.error("Cannot create barrier - server is null");
            return;
        }
        Zoning zoning2 = zoning = zone.zoning;
        synchronized (zoning2) {
            edgeTiles = zone.zoning.getEdgeTiles();
            if (edgeTiles == null || edgeTiles.isEmpty()) {
                ModLogger.warn("Cannot create barrier for zone '%s' - no edge tiles", zone.name);
                return;
            }
        }
        PointHashSet barrierPositions = edgeTiles;
        ModLogger.info("Creating %d barriers on edge tiles of zone '%s'", barrierPositions.size(), zone.name);
        if (barrierPositions.size() > RuntimeConstants.Zones.getMaxBarrierTiles()) {
            ModLogger.warn("Zone '%s' requires %d barriers! This is too large. Skipping barrier creation.", zone.name, barrierPositions.size());
            return;
        }
        int barrierID = PvPZoneBarrierManager.getBarrierObjectID();
        if (barrierID == -1) {
            ModLogger.error("Barrier object not registered!");
            return;
        }
        int placedCount = 0;
        int replacedCount = 0;
        try {
            int batchPlaced = 0;
            int batchReplaced = 0;
            int processedInBatch = 0;
            int batchSize = RuntimeConstants.Zones.getBarrierAddBatchSize();
            for (Point barrierPos : barrierPositions) {
                try {
                    int existingObjectID = level.getObjectID(0, barrierPos.x, barrierPos.y);
                    if (existingObjectID == barrierID) {
                        ++placedCount;
                        ++batchPlaced;
                    } else {
                        boolean replacing = existingObjectID != 0;
                        level.setObject(barrierPos.x, barrierPos.y, barrierID);
                        server.network.sendToClientsWithTile((Packet)new PacketPlaceObject(level, null, 0, barrierPos.x, barrierPos.y, barrierID, 0, false, false), level, barrierPos.x, barrierPos.y);
                        ++placedCount;
                        ++batchPlaced;
                        if (replacing) {
                            ++replacedCount;
                            ++batchReplaced;
                        }
                    }
                }
                catch (Exception tileException) {
                    ModLogger.error("Failed to place barrier at (%d, %d)", barrierPos.x, barrierPos.y);
                }
                if (++processedInBatch < batchSize) continue;
                ModLogger.info("Placed batch: %d barriers (replaced: %d) for zone '%s'", batchPlaced, batchReplaced, zone.name);
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                processedInBatch = 0;
                batchPlaced = 0;
                batchReplaced = 0;
            }
            if (processedInBatch > 0) {
                ModLogger.info("Placed final batch: %d barriers (replaced: %d) for zone '%s'", batchPlaced, batchReplaced, zone.name);
            }
            if (replacedCount > 0) {
                ModLogger.info("Created %d barriers for PVP zone '%s' (replaced %d existing objects)", placedCount, zone.name, replacedCount);
            } else {
                ModLogger.info("Created %d barriers for PVP zone '%s'", placedCount, zone.name);
            }
        }
        catch (Exception e) {
            ModLogger.error("Error creating barriers for zone '" + zone.name + "'", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void removeBarrier(Level level, PvPZone zone) {
        PointHashSet edgeTiles;
        Zoning zoning;
        if (!level.isServer()) {
            return;
        }
        Server server = level.getServer();
        if (server == null) {
            return;
        }
        int barrierID = PvPZoneBarrierManager.getBarrierObjectID();
        if (barrierID == -1) {
            ModLogger.error("Barrier object not registered!");
            return;
        }
        Zoning zoning2 = zoning = zone.zoning;
        synchronized (zoning2) {
            edgeTiles = zone.zoning.getEdgeTiles();
            if (edgeTiles == null || edgeTiles.isEmpty()) {
                ModLogger.info("No edge tiles for zone '%s', cannot remove barriers", zone.name);
                return;
            }
        }
        ModLogger.info("Removing barriers from %d edge tiles of zone '%s'", edgeTiles.size(), zone.name);
        int removedCount = 0;
        for (Point pos : edgeTiles) {
            try {
                int existingObjectID = level.getObjectID(0, pos.x, pos.y);
                if (existingObjectID != barrierID) continue;
                level.setObject(pos.x, pos.y, 0);
                server.network.sendToClientsWithTile((Packet)new PacketPlaceObject(level, null, 0, pos.x, pos.y, 0, 0, false, false), level, pos.x, pos.y);
                ++removedCount;
            }
            catch (Exception e) {
                ModLogger.error("Failed to remove barrier at (%d, %d): %s", pos.x, pos.y, e.getMessage());
            }
        }
        ModLogger.info("Removed %d barriers for zone '%s'", removedCount, zone.name);
    }

    public static void updateBarrier(Level level, PvPZone zone) {
        if (!level.isServer()) {
            return;
        }
        PvPZoneBarrierManager.removeBarrier(level, zone);
        PvPZoneBarrierManager.createBarrier(level, zone);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateBarrier(Level level, PvPZone zone, Collection<Point> oldEdgePositions) {
        HashSet<Point> toRemove;
        HashSet<Point> oldSet;
        HashSet<Point> newSet;
        int barrierID;
        Server server;
        block50: {
            PointHashSet newEdgeTiles;
            Zoning zoning;
            if (!level.isServer()) {
                return;
            }
            server = level.getServer();
            if (server == null) {
                return;
            }
            barrierID = PvPZoneBarrierManager.getBarrierObjectID();
            if (barrierID == -1) {
                ModLogger.error("Barrier object not registered!");
                return;
            }
            Zoning zoning2 = zoning = zone.zoning;
            synchronized (zoning2) {
                newEdgeTiles = zone.zoning.getEdgeTiles();
                if (newEdgeTiles == null) {
                    newEdgeTiles = new PointHashSet();
                }
            }
            newSet = new HashSet<Point>();
            for (Object p : newEdgeTiles) {
                newSet.add(new Point((Point)p));
            }
            oldSet = new HashSet<Point>();
            if (oldEdgePositions != null) {
                for (Point p : oldEdgePositions) {
                    oldSet.add(new Point(p));
                }
            }
            toRemove = new HashSet<Point>(oldSet);
            toRemove.removeAll(newSet);
            try {
                Rectangle bounds = zone.zoning.getTileBounds();
                if (bounds == null) break block50;
                Rectangle sweep = new Rectangle(bounds.x - 1, bounds.y - 1, bounds.width + 2, bounds.height + 2);
                long area = (long)sweep.width * (long)sweep.height;
                long maxArea = Math.max(1000, ZoneConstants.getMaxBarrierTiles() * 4);
                if (area <= maxArea) {
                    for (int sx = sweep.x; sx < sweep.x + sweep.width; ++sx) {
                        for (int sy = sweep.y; sy < sweep.y + sweep.height; ++sy) {
                            try {
                                Point p;
                                int existing = level.getObjectID(0, sx, sy);
                                if (existing != barrierID || newSet.contains(p = new Point(sx, sy))) continue;
                                boolean isEdgeOfOther = false;
                                AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
                                if (zoneData != null) {
                                    Map<Integer, PvPZone> map;
                                    Map<Integer, PvPZone> map2 = map = zoneData.getPvPZonesInternal();
                                    synchronized (map2) {
                                        for (PvPZone other : map.values()) {
                                            PointHashSet otherEdge;
                                            if (other.uniqueID == zone.uniqueID || (otherEdge = other.zoning.getEdgeTiles()) == null || !otherEdge.contains(p.x, p.y)) continue;
                                            isEdgeOfOther = true;
                                            break;
                                        }
                                    }
                                }
                                if (isEdgeOfOther) continue;
                                toRemove.add(p);
                                continue;
                            }
                            catch (Exception existing) {
                                // empty catch block
                            }
                        }
                    }
                    break block50;
                }
                ModLogger.info("Skipping bbox sweep for zone '%s' (area too large: %d)", zone.name, area);
            }
            catch (Exception sweepEx) {
                ModLogger.error("Error during bbox sweep for zone '" + zone.name + "'", sweepEx);
            }
        }
        HashSet toAdd = new HashSet(newSet);
        toAdd.removeAll(oldSet);
        int removedCount = 0;
        int addedCount = 0;
        try {
            int processedRemove = 0;
            int batchSize = ZoneConstants.getBarrierAddBatchSize();
            int batchRemoved = 0;
            for (Point pos : toRemove) {
                try {
                    int existingObjectID = level.getObjectID(0, pos.x, pos.y);
                    if (existingObjectID == barrierID) {
                        level.setObject(pos.x, pos.y, 0);
                        server.network.sendToClientsWithTile((Packet)new PacketPlaceObject(level, null, 0, pos.x, pos.y, 0, 0, false, false), level, pos.x, pos.y);
                        ++removedCount;
                        ++batchRemoved;
                    }
                }
                catch (Exception e) {
                    ModLogger.error("Failed to remove barrier at (%d, %d): %s", pos.x, pos.y, e.getMessage());
                }
                if (++processedRemove < batchSize) continue;
                ModLogger.info("Removed batch: %d barriers for zone '%s'", batchRemoved, zone.name);
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                processedRemove = 0;
                batchRemoved = 0;
            }
            if (processedRemove > 0) {
                ModLogger.info("Removed final batch: %d barriers for zone '%s'", batchRemoved, zone.name);
            }
            if (toAdd.size() > ZoneConstants.getMaxBarrierTiles()) {
                ModLogger.warn("Zone '%s' requires %d barriers to add! This is too large. Skipping barrier creation.", zone.name, toAdd.size());
            } else {
                int processedAdd = 0;
                int batchAdded = 0;
                int batchReplaced = 0;
                for (Point pos : toAdd) {
                    try {
                        int existingObjectID = level.getObjectID(0, pos.x, pos.y);
                        if (existingObjectID == barrierID) {
                            ++addedCount;
                            ++batchAdded;
                        } else {
                            boolean replacing = existingObjectID != 0;
                            level.setObject(pos.x, pos.y, barrierID);
                            server.network.sendToClientsWithTile((Packet)new PacketPlaceObject(level, null, 0, pos.x, pos.y, barrierID, 0, false, false), level, pos.x, pos.y);
                            ++addedCount;
                            ++batchAdded;
                            if (replacing) {
                                ++batchReplaced;
                            }
                        }
                    }
                    catch (Exception tileException) {
                        ModLogger.error("Failed to place barrier at (%d, %d): %s", pos.x, pos.y, tileException.getMessage());
                    }
                    if (++processedAdd < batchSize) continue;
                    ModLogger.info("Added batch: %d barriers (replaced: %d) for zone '%s'", batchAdded, batchReplaced, zone.name);
                    try {
                        Thread.sleep(1L);
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    processedAdd = 0;
                    batchAdded = 0;
                    batchReplaced = 0;
                }
                if (processedAdd > 0) {
                    ModLogger.info("Added final batch: %d barriers (replaced: %d) for zone '%s'", batchAdded, batchReplaced, zone.name);
                }
            }
            ModLogger.info("Updated barriers for PVP zone '%s' (added %d, removed %d)", zone.name, addedCount, removedCount);
        }
        catch (Exception e) {
            ModLogger.error("Error updating barriers for zone '" + zone.name + "'", e);
        }
        try {
            for (ServerClient playerClient : server.getClients()) {
                if (playerClient == null || playerClient.playerMob == null) continue;
                PvPZoneTracker.PlayerPvPState state = PvPZoneTracker.getPlayerState(playerClient);
                boolean previouslyInZone = state.currentZoneID == zone.uniqueID;
                boolean nowInZone = zone.containsPosition(playerClient.playerMob.x, playerClient.playerMob.y);
                long serverTime = server.world.worldEntity.getTime();
                if (previouslyInZone && !nowInZone) {
                    PvPZoneTracker.exitZone(playerClient, serverTime);
                    if (playerClient.pvpEnabled && !server.world.settings.forcedPvP) {
                        playerClient.pvpEnabled = false;
                        server.network.sendToAllClients((Packet)new PacketPlayerPvP(playerClient.slot, false));
                    }
                    playerClient.sendChatMessage("\u00a7cYou have been moved out of a PVP zone due to zone changes");
                    Point outsideTile = PvPZoneTracker.findClosestTileOutsideZone(zone, playerClient.playerMob.x, playerClient.playerMob.y);
                    if (outsideTile == null) continue;
                    float nx = outsideTile.x * 32 + 16;
                    float ny = outsideTile.y * 32 + 16;
                    playerClient.playerMob.dx = 0.0f;
                    playerClient.playerMob.dy = 0.0f;
                    playerClient.playerMob.setPos(nx, ny, true);
                    server.network.sendToClientsWithEntity((Packet)new PacketPlayerMovement(playerClient, true), (RegionPositionGetter)playerClient.playerMob);
                    continue;
                }
                if (previouslyInZone || !nowInZone) continue;
                if (PvPZoneTracker.canReEnter(playerClient, serverTime)) {
                    PvPZoneTracker.enterZone(playerClient, zone);
                    if (!playerClient.pvpEnabled && !server.world.settings.forcedPvP) {
                        playerClient.pvpEnabled = true;
                        server.network.sendToAllClients((Packet)new PacketPlayerPvP(playerClient.slot, true));
                    }
                    if (playerClient.playerMob != null) {
                        playerClient.playerMob.addBuff(new ActiveBuff("pvpimmunity", (Mob)playerClient.playerMob, 5.0f, null), true);
                    }
                    playerClient.sendChatMessage("\u00a7aYou are now inside a PVP zone due to zone changes");
                    continue;
                }
                playerClient.sendChatMessage("\u00a77You cannot re-enter the PVP zone yet due to re-entry cooldown");
            }
        }
        catch (Exception reconcileEx) {
            ModLogger.error("Error reconciling players after barrier update for zone '" + zone.name + "'", reconcileEx);
        }
    }
}

