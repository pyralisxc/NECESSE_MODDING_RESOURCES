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
 *  necesse.level.maps.regionSystem.Region
 */
package medievalsim.packets;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import medievalsim.packets.PacketZoneChanged;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.BarrierPlacementWorker;
import medievalsim.zones.PvPZone;
import medievalsim.zones.ZoneManager;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class PacketCreateZone
extends Packet {
    public boolean isProtectedZone;
    public String zoneName;
    public Rectangle initialArea;

    public PacketCreateZone(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.isProtectedZone = reader.getNextBoolean();
        this.zoneName = reader.getNextString();
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        int width = reader.getNextInt();
        int height = reader.getNextInt();
        this.initialArea = new Rectangle(x, y, width, height);
    }

    public PacketCreateZone(boolean isProtectedZone, String zoneName, Rectangle initialArea) {
        this.isProtectedZone = isProtectedZone;
        this.zoneName = zoneName;
        this.initialArea = initialArea;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextBoolean(isProtectedZone);
        writer.putNextString(zoneName);
        writer.putNextInt(initialArea.x);
        writer.putNextInt(initialArea.y);
        writer.putNextInt(initialArea.width);
        writer.putNextInt(initialArea.height);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            AdminZone zone;
            if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
                ModLogger.warn("Player " + client.getName() + " attempted to create zone without admin permission");
                return;
            }
            Level level = server.world.getLevel(client);
            if (level == null) {
                ModLogger.error("Failed to get level for client " + client.getName() + " in PacketCreateZone");
                return;
            }
            String validatedName = this.zoneName;
            if (validatedName != null && validatedName.length() > 50) {
                validatedName = validatedName.substring(0, 50);
            }
            AdminZone adminZone = zone = this.isProtectedZone ? ZoneManager.createProtectedZone(level, validatedName, client) : ZoneManager.createPvPZone(level, validatedName, client);
            if (zone == null) {
                ModLogger.error("Failed to create zone for client " + client.getName());
                return;
            }
            zone.expand(this.initialArea);
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
            if (zoneData != null) {
                List<AdminZone> affected = zoneData.splitZoneIfDisconnected(zone, level);
                for (AdminZone az : affected) {
                    PointHashSet edge;
                    if (!(az instanceof PvPZone) || (edge = az.zoning.getEdgeTiles()) == null) continue;
                    for (Object o : edge) {
                        if (!(o instanceof Point)) continue;
                        Point p = (Point)o;
                        Region region = level.regionManager.getRegionByTile(p.x, p.y, false);
                        if (region == null) continue;
                        BarrierPlacementWorker.queueZoneRegionPlacement(level, (PvPZone)az, region);
                    }
                }
            }
            ModLogger.info("Created zone " + zone.uniqueID + " (" + zone.name + ") by " + client.getName());
            server.network.sendToAllClients((Packet)new PacketZoneChanged(zone, this.isProtectedZone));
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketCreateZone.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

