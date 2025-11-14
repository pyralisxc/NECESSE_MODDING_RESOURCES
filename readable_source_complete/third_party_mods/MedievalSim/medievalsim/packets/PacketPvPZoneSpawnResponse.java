/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.packet.PacketPlayerPvP
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.LevelIdentifier
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import java.awt.Point;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneTracker;
import medievalsim.zones.ZoneConstants;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.level.maps.Level;

public class PacketPvPZoneSpawnResponse
extends Packet {
    public int zoneID;
    public boolean stayInZone;

    public PacketPvPZoneSpawnResponse(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.stayInZone = reader.getNextBoolean();
    }

    public PacketPvPZoneSpawnResponse(int zoneID, boolean stayInZone) {
        this.zoneID = zoneID;
        this.stayInZone = stayInZone;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextBoolean(stayInZone);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            if (!this.stayInZone) {
                LevelIdentifier spawnLevelIdentifier = server.world.worldEntity.spawnLevelIdentifier;
                Point spawnTile = server.world.worldEntity.spawnTile;
                if (spawnLevelIdentifier != null && spawnTile != null) {
                    client.changeLevel(spawnLevelIdentifier, level -> new Point(spawnTile.x * 32 + 16, spawnTile.y * 32 + 16), true);
                    client.sendChatMessage("Teleported to world spawn");
                    ModLogger.info("Player " + client.getName() + " chose to teleport to spawn from PvP zone " + this.zoneID);
                } else {
                    ModLogger.error("Failed to teleport " + client.getName() + " to spawn - spawn location null");
                }
                return;
            }
            Level level2 = server.world.getLevel(client);
            if (level2 == null) {
                ModLogger.error("Failed to get level for client " + client.getName() + " in PacketPvPZoneSpawnResponse");
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level2);
            if (zoneData == null) {
                ModLogger.error("Failed to get zone data for level " + level2.getIdentifier() + " in PacketPvPZoneSpawnResponse");
                return;
            }
            PvPZone zone = zoneData.getPvPZone(this.zoneID);
            if (zone == null) {
                client.sendChatMessage("This PVP zone no longer exists. Teleporting to world spawn.");
                ModLogger.warn("Player " + client.getName() + " spawned in non-existent PvP zone " + this.zoneID + ", teleporting to spawn");
                LevelIdentifier spawnLevelIdentifier = server.world.worldEntity.spawnLevelIdentifier;
                Point spawnTile = server.world.worldEntity.spawnTile;
                if (spawnLevelIdentifier != null && spawnTile != null) {
                    client.changeLevel(spawnLevelIdentifier, lvl -> new Point(spawnTile.x * 32 + 16, spawnTile.y * 32 + 16), true);
                }
                return;
            }
            PvPZoneTracker.enterZone(client, zone);
            if (!client.pvpEnabled && !server.world.settings.forcedPvP) {
                client.pvpEnabled = true;
                server.network.sendToAllClients((Packet)new PacketPlayerPvP(client.slot, true));
            }
            if (client.playerMob != null) {
                client.playerMob.addBuff(new ActiveBuff("pvpimmunity", (Mob)client.playerMob, ZoneConstants.getPvpSpawnImmunitySeconds(), null), true);
            }
            String damagePercentStr = PvPZone.formatDamagePercent(zone.damageMultiplier);
            client.sendChatMessage("Staying in PVP zone: " + zone.name + " (Damage: " + damagePercentStr + ", Combat Lock: " + zone.combatLockSeconds + "s)");
            ModLogger.info("Player " + client.getName() + " chose to stay in PvP zone " + this.zoneID + " (" + zone.name + ") after spawn");
        }
        catch (Exception e) {
            ModLogger.error("Exception in PacketPvPZoneSpawnResponse.processServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

