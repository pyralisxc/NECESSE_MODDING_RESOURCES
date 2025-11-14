/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import medievalsim.packets.PacketZoneSync;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketRequestZoneSync
extends Packet {
    public PacketRequestZoneSync() {
    }

    public PacketRequestZoneSync(byte[] data) {
        super(data);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            Level level = client.getLevel();
            if (level == null) {
                ModLogger.error("Failed to get level for client " + client.getName() + " in PacketRequestZoneSync");
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
            if (zoneData != null) {
                client.sendPacket((Packet)new PacketZoneSync(zoneData, server));
                ModLogger.info("Sent zone sync to player " + client.getName());
            } else {
                ModLogger.warn("No zone data available for level " + level.getIdentifier() + " requested by " + client.getName());
            }
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketRequestZoneSync.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

