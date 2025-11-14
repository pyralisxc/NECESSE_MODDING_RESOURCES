/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.PointHashSet
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.Level;

public class PacketExpandZone
extends Packet {
    public int zoneID;
    public boolean isProtectedZone;
    public Rectangle expandArea;

    public PacketExpandZone(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.isProtectedZone = reader.getNextBoolean();
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        int width = reader.getNextInt();
        int height = reader.getNextInt();
        this.expandArea = new Rectangle(x, y, width, height);
    }

    public PacketExpandZone(int zoneID, boolean isProtectedZone, Rectangle expandArea) {
        this.zoneID = zoneID;
        this.isProtectedZone = isProtectedZone;
        this.expandArea = expandArea;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextBoolean(isProtectedZone);
        writer.putNextInt(expandArea.x);
        writer.putNextInt(expandArea.y);
        writer.putNextInt(expandArea.width);
        writer.putNextInt(expandArea.height);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            AdminZone zone;
            if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
                ModLogger.warn("Player " + client.getName() + " attempted to expand zone without admin permission");
                return;
            }
            Level level = server.world.getLevel(client);
            if (level == null) {
                ModLogger.error("Failed to get level for client " + client.getName() + " in PacketExpandZone");
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level);
            if (zoneData == null) {
                ModLogger.error("Failed to get zone data for level " + level.getIdentifier() + " in PacketExpandZone");
                return;
            }
            AdminZone adminZone = zone = this.isProtectedZone ? zoneData.getProtectedZone(this.zoneID) : zoneData.getPvPZone(this.zoneID);
            if (zone == null) {
                ModLogger.warn("Attempted to expand non-existent zone ID " + this.zoneID);
                return;
            }
            HashMap<Integer, Collection<Point>> oldEdgesSnapshot = new HashMap<Integer, Collection<Point>>();
            try {
                PointHashSet edge = zone.zoning.getEdgeTiles();
                if (edge != null) {
                    ArrayList<Point> l = new ArrayList<Point>();
                    for (Object o : edge) {
                        if (!(o instanceof Point)) continue;
                        l.add(new Point((Point)o));
                    }
                    oldEdgesSnapshot.put(zone.uniqueID, l);
                }
            }
            catch (Exception edge) {
                // empty catch block
            }
            boolean changed = zone.expand(this.expandArea);
            if (changed) {
                AdminZonesLevelData localZoneData = AdminZonesLevelData.getZoneData(level, false);
                if (localZoneData != null) {
                    localZoneData.resolveAfterZoneChange(zone, level, server, this.isProtectedZone, oldEdgesSnapshot);
                }
                ModLogger.info("Expanded zone " + this.zoneID + " (" + zone.name + ") by " + client.getName());
            }
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketExpandZone.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

