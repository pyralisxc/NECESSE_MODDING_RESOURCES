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
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import medievalsim.packets.PacketZoneChanged;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketConfigurePvPZone
extends Packet {
    public int zoneID;
    public float damageMultiplier;
    public int combatLockSeconds;
    public float dotDamageMultiplier;
    public float dotIntervalMultiplier;

    public PacketConfigurePvPZone(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.damageMultiplier = reader.getNextFloat();
        this.dotDamageMultiplier = reader.getNextFloat();
        this.dotIntervalMultiplier = reader.getNextFloat();
        this.combatLockSeconds = reader.getNextInt();
    }

    public PacketConfigurePvPZone(int zoneID, float damageMultiplier, int combatLockSeconds, float dotDamageMultiplier, float dotIntervalMultiplier) {
        this.zoneID = zoneID;
        this.damageMultiplier = damageMultiplier;
        this.combatLockSeconds = combatLockSeconds;
        this.dotDamageMultiplier = dotDamageMultiplier;
        this.dotIntervalMultiplier = dotIntervalMultiplier;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextFloat(damageMultiplier);
        writer.putNextFloat(dotDamageMultiplier);
        writer.putNextFloat(dotIntervalMultiplier);
        writer.putNextInt(combatLockSeconds);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
                ModLogger.warn("Player " + client.getName() + " attempted to configure PvP zone without admin permission");
                return;
            }
            Level level = server.world.getLevel(client);
            if (level == null) {
                ModLogger.error("Failed to get level for client " + client.getName() + " in PacketConfigurePvPZone");
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level);
            if (zoneData == null) {
                ModLogger.error("Failed to get zone data for level " + level.getIdentifier() + " in PacketConfigurePvPZone");
                return;
            }
            PvPZone zone = zoneData.getPvPZone(this.zoneID);
            if (zone == null) {
                ModLogger.warn("Attempted to configure non-existent PvP zone ID " + this.zoneID);
                return;
            }
            zone.damageMultiplier = Math.max(0.001f, Math.min(0.1f, this.damageMultiplier));
            zone.combatLockSeconds = Math.max(0, Math.min(10, this.combatLockSeconds));
            zone.dotDamageMultiplier = Math.max(0.01f, Math.min(2.0f, this.dotDamageMultiplier));
            zone.dotIntervalMultiplier = Math.max(0.25f, Math.min(4.0f, this.dotIntervalMultiplier));
            ModLogger.info("Configured PvP zone " + this.zoneID + " (" + zone.name + ") by " + client.getName());
            server.network.sendToAllClients((Packet)new PacketZoneChanged(zone, false));
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketConfigurePvPZone.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

