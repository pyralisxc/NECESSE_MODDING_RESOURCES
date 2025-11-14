/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.packet.PacketPlayerMovement
 *  necesse.engine.network.packet.PacketPlayerPvP
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 *  necesse.level.maps.regionSystem.RegionPositionGetter
 */
package medievalsim.packets;

import java.awt.Point;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneTracker;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class PacketPvPZoneExitResponse
extends Packet {
    public int zoneID;
    public boolean acceptExit;

    public PacketPvPZoneExitResponse(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.acceptExit = reader.getNextBoolean();
    }

    public PacketPvPZoneExitResponse(int zoneID, boolean acceptExit) {
        this.zoneID = zoneID;
        this.acceptExit = acceptExit;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextBoolean(acceptExit);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            if (client.playerMob == null) {
                ModLogger.error("PacketPvPZoneExitResponse received for client with null playerMob: " + client.getName());
                return;
            }
            Level level = client.playerMob.getLevel();
            if (level == null) {
                ModLogger.error("Failed to get level for player " + client.getName() + " in PacketPvPZoneExitResponse");
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
            if (zoneData == null) {
                ModLogger.error("Failed to get zone data for level " + level.getIdentifier() + " in PacketPvPZoneExitResponse");
                return;
            }
            PvPZone zone = zoneData.getPvPZone(this.zoneID);
            if (zone == null) {
                client.sendChatMessage("\u00a7cError: PVP zone no longer exists");
                ModLogger.warn("Player " + client.getName() + " attempted to exit non-existent PvP zone ID " + this.zoneID);
                return;
            }
            if (this.acceptExit) {
                long serverTime = server.world.worldEntity.getTime();
                if (zone.combatLockSeconds > 0 && PvPZoneTracker.isInCombat(client, level, serverTime)) {
                    int remainingSeconds = PvPZoneTracker.getRemainingCombatLockSeconds(client, level, serverTime);
                    client.sendChatMessage("\u00a7cYou cannot leave while in combat! (" + remainingSeconds + "s remaining)");
                    return;
                }
                Point exitTile = PvPZoneTracker.findClosestTileOutsideZone(zone, client.playerMob.x, client.playerMob.y);
                if (exitTile != null) {
                    float exitX = exitTile.x * 32 + 16;
                    float exitY = exitTile.y * 32 + 16;
                    client.playerMob.dx = 0.0f;
                    client.playerMob.dy = 0.0f;
                    client.playerMob.setPos(exitX, exitY, true);
                    server.network.sendToClientsWithEntity((Packet)new PacketPlayerMovement(client, true), (RegionPositionGetter)client.playerMob);
                }
                PvPZoneTracker.exitZone(client, serverTime);
                if (client.pvpEnabled && !server.world.settings.forcedPvP) {
                    client.pvpEnabled = false;
                    server.network.sendToAllClients((Packet)new PacketPlayerPvP(client.slot, false));
                }
                client.sendChatMessage("\u00a7aExited PVP zone: " + zone.name);
                ModLogger.info("Player " + client.getName() + " exited PvP zone " + this.zoneID + " (" + zone.name + ")");
            } else {
                client.sendChatMessage("\u00a77You chose to stay in the PVP zone");
            }
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketPvPZoneExitResponse.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

