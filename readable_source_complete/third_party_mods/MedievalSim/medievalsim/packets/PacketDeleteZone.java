/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.packet.PacketPlayerPvP
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import medievalsim.packets.PacketZoneRemoved;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneTracker;
import medievalsim.zones.ZoneManager;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketDeleteZone
extends Packet {
    public int zoneID;
    public boolean isProtectedZone;

    public PacketDeleteZone(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.isProtectedZone = reader.getNextBoolean();
    }

    public PacketDeleteZone(int zoneID, boolean isProtectedZone) {
        this.zoneID = zoneID;
        this.isProtectedZone = isProtectedZone;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextBoolean(isProtectedZone);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            AdminZone zone;
            if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
                ModLogger.warn("Player " + client.getName() + " attempted to delete zone without admin permission");
                return;
            }
            Level level = server.world.getLevel(client);
            if (level == null) {
                ModLogger.error("Failed to get level for client " + client.getName() + " in PacketDeleteZone");
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level);
            if (zoneData == null) {
                ModLogger.error("Failed to get zone data for level " + level.getIdentifier() + " in PacketDeleteZone");
                return;
            }
            AdminZone adminZone = zone = this.isProtectedZone ? zoneData.getProtectedZone(this.zoneID) : zoneData.getPvPZone(this.zoneID);
            if (zone == null) {
                ModLogger.warn("Attempted to delete non-existent zone ID " + this.zoneID + " (protected=" + this.isProtectedZone + ")");
                return;
            }
            if (!this.isProtectedZone && zone instanceof PvPZone) {
                ((PvPZone)zone).removeBarriers(level);
            }
            if (!this.isProtectedZone) {
                long serverTime = server.world.worldEntity.getTime();
                for (ServerClient playerClient : server.getClients()) {
                    PvPZoneTracker.PlayerPvPState state = PvPZoneTracker.getPlayerState(playerClient);
                    if (state.currentZoneID != this.zoneID) continue;
                    PvPZoneTracker.exitZone(playerClient, serverTime);
                    if (playerClient.pvpEnabled && !server.world.settings.forcedPvP) {
                        playerClient.pvpEnabled = false;
                        server.network.sendToAllClients((Packet)new PacketPlayerPvP(playerClient.slot, false));
                    }
                    playerClient.sendChatMessage("\u00a7cPVP zone deleted - you have been removed from the zone");
                }
            }
            if (this.isProtectedZone) {
                ZoneManager.deleteProtectedZone(level, this.zoneID);
            } else {
                ZoneManager.deletePvPZone(level, this.zoneID);
            }
            ModLogger.info("Deleted zone " + this.zoneID + " (" + zone.name + ") by " + client.getName());
            server.network.sendToAllClients((Packet)new PacketZoneRemoved(this.zoneID, this.isProtectedZone));
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketDeleteZone.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

