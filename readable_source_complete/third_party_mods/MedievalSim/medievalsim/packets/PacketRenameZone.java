/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import medievalsim.packets.PacketZoneChanged;
import medievalsim.packets.PacketZoneRemoved;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketRenameZone
extends Packet {
    public final int zoneUniqueID;
    public final boolean isProtectedZone;
    public final GameMessage newName;

    public PacketRenameZone(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneUniqueID = reader.getNextInt();
        this.isProtectedZone = reader.getNextBoolean();
        this.newName = GameMessage.fromPacket((PacketReader)reader);
    }

    public PacketRenameZone(int zoneUniqueID, boolean isProtectedZone, GameMessage newName) {
        this.zoneUniqueID = zoneUniqueID;
        this.isProtectedZone = isProtectedZone;
        this.newName = newName;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneUniqueID);
        writer.putNextBoolean(isProtectedZone);
        newName.writePacket(writer);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            AdminZone zone;
            if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
                ModLogger.warn("Player " + client.getName() + " attempted to rename zone without admin permission");
                return;
            }
            Level level = server.world.getLevel(client.playerMob.getLevel().getIdentifier());
            if (level == null) {
                ModLogger.error("Failed to get level for client " + client.getName() + " in PacketRenameZone");
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
            if (zoneData == null) {
                ModLogger.error("Failed to get zone data for level " + level.getIdentifier() + " in PacketRenameZone");
                return;
            }
            AdminZone adminZone = zone = this.isProtectedZone ? (AdminZone)zoneData.getProtectedZones().get(this.zoneUniqueID) : (AdminZone)zoneData.getPvPZones().get(this.zoneUniqueID);
            if (zone == null) {
                ModLogger.warn("Attempted to rename non-existent zone ID " + this.zoneUniqueID);
                PacketZoneRemoved removedPacket = new PacketZoneRemoved(this.zoneUniqueID, this.isProtectedZone);
                client.sendPacket((Packet)removedPacket);
                return;
            }
            String translatedName = this.newName.translate();
            if (translatedName != null && translatedName.length() > 50) {
                translatedName = translatedName.substring(0, 50);
            }
            zone.name = translatedName;
            ModLogger.info("Renamed zone " + this.zoneUniqueID + " to '" + translatedName + "' by " + client.getName());
            PacketZoneChanged changedPacket = new PacketZoneChanged(zone, this.isProtectedZone);
            level.getServer().network.sendToAllClients((Packet)changedPacket);
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketRenameZone.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void processClient(NetworkPacket packet, Client client) {
    }
}

