/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.Zoning
 *  necesse.level.maps.Level
 */
package medievalsim.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import medievalsim.ui.AdminToolsHudForm;
import medievalsim.ui.AdminToolsHudManager;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.ProtectedZone;
import medievalsim.zones.PvPZone;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.Zoning;
import necesse.level.maps.Level;

public class PacketZoneSync
extends Packet {
    public List<ZoneData> protectedZones;
    public List<ZoneData> pvpZones;

    public PacketZoneSync(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        int protectedCount = reader.getNextShortUnsigned();
        this.protectedZones = new ArrayList<ZoneData>(protectedCount);
        for (int i = 0; i < protectedCount; ++i) {
            ZoneData zoneData = new ZoneData();
            zoneData.read(reader, false);
            this.protectedZones.add(zoneData);
        }
        int pvpCount = reader.getNextShortUnsigned();
        this.pvpZones = new ArrayList<ZoneData>(pvpCount);
        for (int i = 0; i < pvpCount; ++i) {
            ZoneData zoneData = new ZoneData();
            zoneData.read(reader, true);
            this.pvpZones.add(zoneData);
        }
    }

    public PacketZoneSync(AdminZonesLevelData zoneData) {
        this(zoneData, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PacketZoneSync(AdminZonesLevelData zoneData, Server server) {
        Map<Integer, ProtectedZone> map;
        Map<Integer, ProtectedZone> protectedMap;
        this.protectedZones = new ArrayList<ZoneData>();
        this.pvpZones = new ArrayList<ZoneData>();
        if (server != null) {
            map = protectedMap = zoneData.getProtectedZonesInternal();
            synchronized (map) {
                for (ProtectedZone zone : protectedMap.values()) {
                    ServerClient ownerClient;
                    if (zone.getOwnerAuth() == -1L || (ownerClient = server.getClientByAuth(zone.getOwnerAuth())) == null) continue;
                    zone.setOwnerName(ownerClient.getName());
                }
            }
        }
        map = protectedMap = zoneData.getProtectedZonesInternal();
        synchronized (map) {
            for (ProtectedZone protectedZone : protectedMap.values()) {
                this.protectedZones.add(new ZoneData(protectedZone));
            }
        }
        Map<Integer, PvPZone> pvpMap = zoneData.getPvPZonesInternal();
        Map<Integer, PvPZone> map2 = pvpMap;
        synchronized (map2) {
            for (PvPZone pvPZone : pvpMap.values()) {
                this.pvpZones.add(new ZoneData(pvPZone));
            }
        }
        PacketWriter writer = new PacketWriter((Packet)this);
        if (this.protectedZones.size() > 65535) {
            throw new IllegalStateException("Too many protected zones to sync: " + this.protectedZones.size() + " (max 65535)");
        }
        if (this.pvpZones.size() > 65535) {
            throw new IllegalStateException("Too many PVP zones to sync: " + this.pvpZones.size() + " (max 65535)");
        }
        writer.putNextShortUnsigned(this.protectedZones.size());
        for (ZoneData zoneData2 : this.protectedZones) {
            zoneData2.write(writer, false);
        }
        writer.putNextShortUnsigned(this.pvpZones.size());
        for (ZoneData zoneData3 : this.pvpZones) {
            zoneData3.write(writer, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processClient(NetworkPacket packet, Client client) {
        Object pz;
        Zoning zoning;
        PacketReader zoningReader;
        Packet tempPacket;
        AdminZone zone;
        Level level = client.getLevel();
        if (level == null) {
            return;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, true);
        if (zoneData == null) {
            return;
        }
        zoneData.clearProtectedZones();
        zoneData.clearPvPZones();
        for (ZoneData data : this.protectedZones) {
            zone = new ProtectedZone(data.uniqueID, data.name, data.creatorAuth, data.colorHue);
            tempPacket = new Packet(data.zoningData);
            zoningReader = new PacketReader(tempPacket);
            Zoning zoning2 = zoning = zone.zoning;
            synchronized (zoning2) {
                zone.zoning.readZonePacket(zoningReader);
            }
            if (zone instanceof ProtectedZone) {
                pz = zone;
                if (data.allowedTeamIDs != null) {
                    ((ProtectedZone)pz).allowedTeamIDs.addAll(data.allowedTeamIDs);
                }
                ((ProtectedZone)pz).setOwnerAuth(data.ownerAuth);
                ((ProtectedZone)pz).setOwnerName(data.ownerName);
                ((ProtectedZone)pz).setAllowOwnerTeam(data.allowOwnerTeam);
                ((ProtectedZone)pz).setCanBreak(data.canBreak);
                ((ProtectedZone)pz).setCanPlace(data.canPlace);
                ((ProtectedZone)pz).setCanInteractDoors(data.canInteractDoors);
                ((ProtectedZone)pz).setCanInteractContainers(data.canInteractContainers);
                ((ProtectedZone)pz).setCanInteractStations(data.canInteractStations);
                ((ProtectedZone)pz).setCanInteractSigns(data.canInteractSigns);
                ((ProtectedZone)pz).setCanInteractSwitches(data.canInteractSwitches);
                ((ProtectedZone)pz).setCanInteractFurniture(data.canInteractFurniture);
            }
            zoneData.putProtectedZone((ProtectedZone)zone);
        }
        for (ZoneData data : this.pvpZones) {
            zone = new PvPZone(data.uniqueID, data.name, data.creatorAuth, data.colorHue, data.damageMultiplier, data.combatLockSeconds);
            tempPacket = new Packet(data.zoningData);
            zoningReader = new PacketReader(tempPacket);
            zoning = ((PvPZone)zone).zoning;
            pz = zoning;
            synchronized (pz) {
                ((PvPZone)zone).zoning.readZonePacket(zoningReader);
            }
            zoneData.putPvPZone((PvPZone)zone);
        }
        AdminToolsHudForm hudForm = AdminToolsHudManager.getHudForm();
        if (hudForm != null) {
            AdminZone zone2;
            HashMap<Integer, ProtectedZone> protectedMap = new HashMap<Integer, ProtectedZone>();
            HashMap<Integer, PvPZone> pvpMap = new HashMap<Integer, PvPZone>();
            for (ZoneData data : this.protectedZones) {
                zone2 = zoneData.getProtectedZones().get(data.uniqueID);
                if (zone2 == null) continue;
                protectedMap.put(zone2.uniqueID, (ProtectedZone)zone2);
            }
            for (ZoneData data : this.pvpZones) {
                zone2 = zoneData.getPvPZones().get(data.uniqueID);
                if (zone2 == null) continue;
                pvpMap.put(((PvPZone)zone2).uniqueID, (PvPZone)zone2);
            }
            hudForm.updateZones(protectedMap, pvpMap);
        }
    }

    public static class ZoneData {
        public int uniqueID;
        public String name;
        public long creatorAuth;
        public int colorHue;
        public byte[] zoningData;
        public HashSet<Integer> allowedTeamIDs;
        public float damageMultiplier;
        public int combatLockSeconds;
        public long ownerAuth;
        public String ownerName;
        public boolean allowOwnerTeam;
        public boolean canBreak;
        public boolean canPlace;
        public boolean canInteractDoors;
        public boolean canInteractContainers;
        public boolean canInteractStations;
        public boolean canInteractSigns;
        public boolean canInteractSwitches;
        public boolean canInteractFurniture;

        public ZoneData() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public ZoneData(AdminZone zone) {
            Zoning zoning;
            this.uniqueID = zone.uniqueID;
            this.name = zone.name;
            this.creatorAuth = zone.creatorAuth;
            this.colorHue = zone.colorHue;
            Packet tempPacket = new Packet();
            PacketWriter zoningWriter = new PacketWriter(tempPacket);
            Zoning zoning2 = zoning = zone.zoning;
            synchronized (zoning2) {
                zone.zoning.writeZonePacket(zoningWriter);
            }
            this.zoningData = tempPacket.getPacketData();
            if (zone instanceof ProtectedZone) {
                ProtectedZone protectedZone = (ProtectedZone)zone;
                this.allowedTeamIDs = new HashSet<Integer>(protectedZone.allowedTeamIDs);
                this.ownerAuth = protectedZone.getOwnerAuth();
                this.ownerName = protectedZone.getOwnerName();
                this.allowOwnerTeam = protectedZone.getAllowOwnerTeam();
                this.canBreak = protectedZone.getCanBreak();
                this.canPlace = protectedZone.getCanPlace();
                this.canInteractDoors = protectedZone.getCanInteractDoors();
                this.canInteractContainers = protectedZone.getCanInteractContainers();
                this.canInteractStations = protectedZone.getCanInteractStations();
                this.canInteractSigns = protectedZone.getCanInteractSigns();
                this.canInteractSwitches = protectedZone.getCanInteractSwitches();
                this.canInteractFurniture = protectedZone.getCanInteractFurniture();
            }
            if (zone instanceof PvPZone) {
                PvPZone pvpZone = (PvPZone)zone;
                this.damageMultiplier = pvpZone.damageMultiplier;
                this.combatLockSeconds = pvpZone.combatLockSeconds;
            }
        }

        public void write(PacketWriter writer, boolean isPvPZone) {
            writer.putNextInt(this.uniqueID);
            writer.putNextString(this.name);
            writer.putNextLong(this.creatorAuth);
            writer.putNextInt(this.colorHue);
            writer.putNextInt(this.zoningData.length);
            writer.putNextBytes(this.zoningData);
            if (this.allowedTeamIDs != null) {
                writer.putNextShortUnsigned(this.allowedTeamIDs.size());
                for (int teamID : this.allowedTeamIDs) {
                    writer.putNextInt(teamID);
                }
            } else {
                writer.putNextShortUnsigned(0);
            }
            if (isPvPZone) {
                writer.putNextFloat(this.damageMultiplier);
                writer.putNextInt(this.combatLockSeconds);
            } else {
                writer.putNextLong(this.ownerAuth);
                writer.putNextString(this.ownerName);
                writer.putNextBoolean(this.allowOwnerTeam);
                writer.putNextBoolean(this.canBreak);
                writer.putNextBoolean(this.canPlace);
                writer.putNextBoolean(this.canInteractDoors);
                writer.putNextBoolean(this.canInteractContainers);
                writer.putNextBoolean(this.canInteractStations);
                writer.putNextBoolean(this.canInteractSigns);
                writer.putNextBoolean(this.canInteractSwitches);
                writer.putNextBoolean(this.canInteractFurniture);
            }
        }

        public void read(PacketReader reader, boolean isPvPZone) {
            this.uniqueID = reader.getNextInt();
            this.name = reader.getNextString();
            this.creatorAuth = reader.getNextLong();
            this.colorHue = reader.getNextInt();
            int zoningDataLength = reader.getNextInt();
            this.zoningData = reader.getNextBytes(zoningDataLength);
            int teamCount = reader.getNextShortUnsigned();
            if (teamCount > 0) {
                this.allowedTeamIDs = new HashSet();
                for (int i = 0; i < teamCount; ++i) {
                    this.allowedTeamIDs.add(reader.getNextInt());
                }
            }
            if (isPvPZone) {
                this.damageMultiplier = reader.getNextFloat();
                this.combatLockSeconds = reader.getNextInt();
            } else {
                this.ownerAuth = reader.getNextLong();
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
        }
    }
}

