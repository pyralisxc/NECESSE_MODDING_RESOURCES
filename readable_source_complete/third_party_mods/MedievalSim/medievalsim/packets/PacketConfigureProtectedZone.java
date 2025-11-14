/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import medievalsim.packets.PacketZoneSync;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.ProtectedZone;
import medievalsim.zones.ZoneManager;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketConfigureProtectedZone
extends Packet {
    public final int zoneID;
    public final String ownerName;
    public final boolean allowOwnerTeam;
    public final boolean canBreak;
    public final boolean canPlace;
    public final boolean canInteractDoors;
    public final boolean canInteractContainers;
    public final boolean canInteractStations;
    public final boolean canInteractSigns;
    public final boolean canInteractSwitches;
    public final boolean canInteractFurniture;

    public PacketConfigureProtectedZone(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.ownerName = reader.getNextString();
        this.allowOwnerTeam = reader.getNextBoolean();
        this.canBreak = reader.getNextBoolean();
        this.canPlace = reader.getNextBoolean();
        this.canInteractDoors = reader.getNextBoolean();
        this.canInteractContainers = reader.getNextBoolean();
        this.canInteractStations = reader.getNextBoolean();
        this.canInteractSigns = reader.getNextBoolean();
        this.canInteractSwitches = reader.getNextBoolean();
        this.canInteractFurniture = reader.getNextBoolean();
    }

    public PacketConfigureProtectedZone(int zoneID, String ownerName, boolean allowOwnerTeam, boolean canBreak, boolean canPlace, boolean canInteractDoors, boolean canInteractContainers, boolean canInteractStations, boolean canInteractSigns, boolean canInteractSwitches, boolean canInteractFurniture) {
        this.zoneID = zoneID;
        this.ownerName = ownerName != null ? ownerName : "";
        this.allowOwnerTeam = allowOwnerTeam;
        this.canBreak = canBreak;
        this.canPlace = canPlace;
        this.canInteractDoors = canInteractDoors;
        this.canInteractContainers = canInteractContainers;
        this.canInteractStations = canInteractStations;
        this.canInteractSigns = canInteractSigns;
        this.canInteractSwitches = canInteractSwitches;
        this.canInteractFurniture = canInteractFurniture;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextString(this.ownerName);
        writer.putNextBoolean(allowOwnerTeam);
        writer.putNextBoolean(canBreak);
        writer.putNextBoolean(canPlace);
        writer.putNextBoolean(canInteractDoors);
        writer.putNextBoolean(canInteractContainers);
        writer.putNextBoolean(canInteractStations);
        writer.putNextBoolean(canInteractSigns);
        writer.putNextBoolean(canInteractSwitches);
        writer.putNextBoolean(canInteractFurniture);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            if (client.getPermissionLevel().getLevel() < 2) {
                ModLogger.warn("Client " + client.getName() + " attempted to configure protected zone without permissions");
                return;
            }
            if (client.playerMob == null || client.playerMob.getLevel() == null) {
                ModLogger.warn("PacketConfigureProtectedZone: Client has no level");
                return;
            }
            Level level = client.playerMob.getLevel();
            ProtectedZone zone = ZoneManager.getProtectedZone(level, this.zoneID);
            if (zone == null) {
                ModLogger.warn("PacketConfigureProtectedZone: Zone " + this.zoneID + " not found");
                return;
            }
            long ownerAuth = -1L;
            String resolvedOwnerName = "";
            if (!this.ownerName.isEmpty()) {
                ServerClient ownerClient = null;
                for (ServerClient sc : server.getClients()) {
                    if (!sc.getName().equalsIgnoreCase(this.ownerName)) continue;
                    ownerClient = sc;
                    break;
                }
                if (ownerClient != null) {
                    ownerAuth = ownerClient.authentication;
                    resolvedOwnerName = ownerClient.getName();
                    ModLogger.info("Resolved owner '" + this.ownerName + "' to auth " + ownerAuth + " (name: " + resolvedOwnerName + ")");
                } else {
                    try {
                        ownerAuth = Long.parseLong(this.ownerName);
                        ServerClient authClient = server.getClientByAuth(ownerAuth);
                        if (authClient != null) {
                            resolvedOwnerName = authClient.getName();
                            ModLogger.info("Resolved auth ID " + ownerAuth + " to online player '" + resolvedOwnerName + "'");
                        } else {
                            ModLogger.info("Using direct auth ID: " + ownerAuth + " (player offline, name will be empty)");
                        }
                    }
                    catch (NumberFormatException ex) {
                        ModLogger.warn("Owner '" + this.ownerName + "' not found online and not a valid auth ID");
                        client.sendChatMessage("Player '" + this.ownerName + "' not found. They must be online to set as owner.");
                        return;
                    }
                }
            }
            zone.setOwnerAuth(ownerAuth);
            zone.setOwnerName(resolvedOwnerName);
            zone.setAllowOwnerTeam(this.allowOwnerTeam);
            zone.setCanBreak(this.canBreak);
            zone.setCanPlace(this.canPlace);
            zone.setCanInteractDoors(this.canInteractDoors);
            zone.setCanInteractContainers(this.canInteractContainers);
            zone.setCanInteractStations(this.canInteractStations);
            zone.setCanInteractSigns(this.canInteractSigns);
            zone.setCanInteractSwitches(this.canInteractSwitches);
            zone.setCanInteractFurniture(this.canInteractFurniture);
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level);
            if (zoneData != null) {
                server.network.sendToAllClients((Packet)new PacketZoneSync(zoneData, server));
            }
            ModLogger.info("Zone " + this.zoneID + " configured: owner=" + (String)(resolvedOwnerName.isEmpty() ? "auth:" + ownerAuth : resolvedOwnerName) + " (auth=" + ownerAuth + "), allowTeam=" + this.allowOwnerTeam + ", break=" + this.canBreak + ", place=" + this.canPlace + ", doors=" + this.canInteractDoors + ", containers=" + this.canInteractContainers + ", stations=" + this.canInteractStations + ", signs=" + this.canInteractSigns + ", switches=" + this.canInteractSwitches + ", furniture=" + this.canInteractFurniture);
        }
        catch (Exception ex) {
            ModLogger.error("Error configuring protected zone: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

