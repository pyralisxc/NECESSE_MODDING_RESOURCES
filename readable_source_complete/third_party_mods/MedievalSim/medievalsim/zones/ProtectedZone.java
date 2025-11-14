/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.SignObject
 *  necesse.level.gameObject.container.CraftingStationObject
 *  necesse.level.gameObject.container.FueledCraftingStationObject
 *  necesse.level.gameObject.container.InventoryObject
 *  necesse.level.gameObject.furniture.FurnitureObject
 *  necesse.level.maps.Level
 */
package medievalsim.zones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import medievalsim.zones.AdminZone;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SignObject;
import necesse.level.gameObject.container.CraftingStationObject;
import necesse.level.gameObject.container.FueledCraftingStationObject;
import necesse.level.gameObject.container.InventoryObject;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;

public class ProtectedZone
extends AdminZone {
    public static final String TYPE_ID = "protected";
    public Set<Integer> allowedTeamIDs = new HashSet<Integer>();
    private long ownerAuth = -1L;
    private String ownerName = "";
    private boolean allowOwnerTeam = true;
    private boolean canBreak = false;
    private boolean canPlace = false;
    private boolean canInteractDoors = false;
    private boolean canInteractContainers = false;
    private boolean canInteractStations = false;
    private boolean canInteractSigns = false;
    private boolean canInteractSwitches = false;
    private boolean canInteractFurniture = false;

    public ProtectedZone() {
    }

    public ProtectedZone(int uniqueID, String name, long creatorAuth, int colorHue) {
        super(uniqueID, name, creatorAuth, colorHue);
        this.ownerAuth = creatorAuth;
    }

    @Override
    public String getTypeID() {
        return TYPE_ID;
    }

    public boolean canClientModify(ServerClient client, Level level) {
        return this.canClientBreak(client, level) && this.canClientPlace(client, level);
    }

    public boolean canClientBreak(ServerClient client, Level level) {
        if (client == null) {
            return false;
        }
        if (this.isWorldOwner(client, level)) {
            return true;
        }
        if (this.isOwner(client)) {
            return true;
        }
        if (this.isCreator(client)) {
            return true;
        }
        if (this.allowOwnerTeam && this.isOnOwnerTeam(client, level)) {
            return this.canBreak;
        }
        int clientTeamID = client.getTeamID();
        if (clientTeamID != -1 && this.allowedTeamIDs.contains(clientTeamID)) {
            return this.canBreak;
        }
        return false;
    }

    public boolean canClientPlace(ServerClient client, Level level) {
        if (client == null) {
            return false;
        }
        if (this.isWorldOwner(client, level)) {
            return true;
        }
        if (this.isOwner(client)) {
            return true;
        }
        if (this.isCreator(client)) {
            return true;
        }
        if (this.allowOwnerTeam && this.isOnOwnerTeam(client, level)) {
            return this.canPlace;
        }
        int clientTeamID = client.getTeamID();
        if (clientTeamID != -1 && this.allowedTeamIDs.contains(clientTeamID)) {
            return this.canPlace;
        }
        return false;
    }

    public boolean canClientInteract(ServerClient client, Level level) {
        return this.canClientBreak(client, level) || this.canClientPlace(client, level);
    }

    public boolean canClientInteract(ServerClient client, Level level, GameObject gameObject) {
        if (client == null || gameObject == null) {
            return false;
        }
        if (this.isWorldOwner(client, level)) {
            return true;
        }
        if (this.isOwner(client)) {
            return true;
        }
        if (this.isCreator(client)) {
            return true;
        }
        boolean hasPermission = false;
        if (gameObject.isDoor) {
            hasPermission = this.canInteractDoors;
        } else if (gameObject instanceof CraftingStationObject || gameObject instanceof FueledCraftingStationObject) {
            hasPermission = this.canInteractStations;
        } else if (gameObject instanceof InventoryObject) {
            hasPermission = this.canInteractContainers;
        } else if (gameObject instanceof SignObject) {
            hasPermission = this.canInteractSigns;
        } else if (gameObject.isSwitch || gameObject.isPressurePlate) {
            hasPermission = this.canInteractSwitches;
        } else if (gameObject instanceof FurnitureObject) {
            hasPermission = this.canInteractFurniture;
        } else {
            System.out.println("DEBUG: Unknown GameObject type in protected zone: " + gameObject.getClass().getSimpleName() + " (ID: " + gameObject.getStringID() + ")");
            return false;
        }
        if (this.allowOwnerTeam && this.isOnOwnerTeam(client, level)) {
            return hasPermission;
        }
        int clientTeamID = client.getTeamID();
        if (clientTeamID != -1 && this.allowedTeamIDs.contains(clientTeamID)) {
            return hasPermission;
        }
        return false;
    }

    private boolean isOnOwnerTeam(ServerClient client, Level level) {
        if (this.ownerAuth == -1L) {
            return false;
        }
        if (level == null || level.getServer() == null) {
            return false;
        }
        int ownerTeamID = level.getServer().world.getTeams().getPlayerTeamID(this.ownerAuth);
        if (ownerTeamID == -1) {
            return false;
        }
        int clientTeamID = client.getTeamID();
        return clientTeamID != -1 && clientTeamID == ownerTeamID;
    }

    private boolean isOwner(ServerClient client) {
        if (this.ownerAuth == -1L) {
            return false;
        }
        return client.authentication == this.ownerAuth;
    }

    public long getOwnerAuth() {
        return this.ownerAuth;
    }

    public void setOwnerAuth(long auth) {
        this.ownerAuth = auth;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String name) {
        this.ownerName = name != null ? name : "";
    }

    public boolean getAllowOwnerTeam() {
        return this.allowOwnerTeam;
    }

    public void setAllowOwnerTeam(boolean value) {
        this.allowOwnerTeam = value;
    }

    public boolean getCanBreak() {
        return this.canBreak;
    }

    public void setCanBreak(boolean value) {
        this.canBreak = value;
    }

    public boolean getCanPlace() {
        return this.canPlace;
    }

    public void setCanPlace(boolean value) {
        this.canPlace = value;
    }

    public boolean getCanInteractDoors() {
        return this.canInteractDoors;
    }

    public void setCanInteractDoors(boolean value) {
        this.canInteractDoors = value;
    }

    public boolean getCanInteractContainers() {
        return this.canInteractContainers;
    }

    public void setCanInteractContainers(boolean value) {
        this.canInteractContainers = value;
    }

    public boolean getCanInteractStations() {
        return this.canInteractStations;
    }

    public void setCanInteractStations(boolean value) {
        this.canInteractStations = value;
    }

    public boolean getCanInteractSigns() {
        return this.canInteractSigns;
    }

    public void setCanInteractSigns(boolean value) {
        this.canInteractSigns = value;
    }

    public boolean getCanInteractSwitches() {
        return this.canInteractSwitches;
    }

    public void setCanInteractSwitches(boolean value) {
        this.canInteractSwitches = value;
    }

    public boolean getCanInteractFurniture() {
        return this.canInteractFurniture;
    }

    public void setCanInteractFurniture(boolean value) {
        this.canInteractFurniture = value;
    }

    public String getOwnerDisplayName() {
        if (this.ownerAuth == -1L) {
            return "None";
        }
        if (this.ownerName != null && !this.ownerName.isEmpty()) {
            return this.ownerName;
        }
        return String.valueOf(this.ownerAuth);
    }

    public String getOwnerName(Server server) {
        if (this.ownerAuth == -1L) {
            return "None";
        }
        if (server == null) {
            return "ID:" + this.ownerAuth;
        }
        ServerClient client = server.getClientByAuth(this.ownerAuth);
        if (client != null) {
            return client.getName();
        }
        return "ID:" + this.ownerAuth;
    }

    public void addAllowedTeam(int teamID) {
        if (teamID != -1) {
            this.allowedTeamIDs.add(teamID);
        }
    }

    public void removeAllowedTeam(int teamID) {
        this.allowedTeamIDs.remove(teamID);
    }

    public void clearAllowedTeams() {
        this.allowedTeamIDs.clear();
    }

    public void setAllInteractionPermissions(boolean value) {
        this.canInteractDoors = value;
        this.canInteractContainers = value;
        this.canInteractStations = value;
        this.canInteractSigns = value;
        this.canInteractSwitches = value;
        this.canInteractFurniture = value;
    }

    public void enableVisitorPermissions() {
        this.canBreak = false;
        this.canPlace = false;
        this.canInteractDoors = true;
        this.canInteractContainers = false;
        this.canInteractStations = false;
        this.canInteractSigns = false;
        this.canInteractSwitches = false;
        this.canInteractFurniture = true;
    }

    public void enableTrustedMemberPermissions() {
        this.canBreak = true;
        this.canPlace = true;
        this.canInteractDoors = true;
        this.canInteractContainers = true;
        this.canInteractStations = true;
        this.canInteractSigns = false;
        this.canInteractSwitches = true;
        this.canInteractFurniture = true;
    }

    public void enableFullPermissions() {
        this.canBreak = true;
        this.canPlace = true;
        this.setAllInteractionPermissions(true);
    }

    public String getPermissionSummary() {
        StringBuilder summary = new StringBuilder();
        if (this.canBreak && this.canPlace) {
            summary.append("Build & Break, ");
        } else if (this.canBreak) {
            summary.append("Break only, ");
        } else if (this.canPlace) {
            summary.append("Place only, ");
        }
        int interactionCount = 0;
        if (this.canInteractDoors) {
            ++interactionCount;
        }
        if (this.canInteractContainers) {
            ++interactionCount;
        }
        if (this.canInteractStations) {
            ++interactionCount;
        }
        if (this.canInteractSigns) {
            ++interactionCount;
        }
        if (this.canInteractSwitches) {
            ++interactionCount;
        }
        if (this.canInteractFurniture) {
            ++interactionCount;
        }
        if (interactionCount == 6) {
            summary.append("All Interactions");
        } else if (interactionCount == 0) {
            summary.append("No Interactions");
        } else {
            summary.append(interactionCount).append("/6 Interactions");
        }
        if (summary.length() == 0) {
            return "No Permissions";
        }
        return summary.toString();
    }

    public List<String> getConfigurationWarnings() {
        ArrayList<String> warnings = new ArrayList<String>();
        if (!(this.canBreak || this.canPlace || this.allowOwnerTeam)) {
            warnings.add("Zone allows no building and team access is disabled - only owner can modify");
        }
        if (this.canInteractContainers && !this.canInteractDoors) {
            warnings.add("Container access enabled but door access disabled - users may not reach containers");
        }
        if (this.canInteractStations && !this.canInteractContainers) {
            warnings.add("Crafting station access enabled but container access disabled - limited crafting functionality");
        }
        if (this.canBreak && !this.canPlace) {
            warnings.add("Break enabled but place disabled - users can destroy but not rebuild");
        }
        if (this.allowedTeamIDs.isEmpty() && !this.allowOwnerTeam) {
            warnings.add("No teams allowed and owner team disabled - only owner has access");
        }
        return warnings;
    }

    public boolean hasProtections() {
        return !this.canBreak || !this.canPlace || !this.canInteractDoors || !this.canInteractContainers || !this.canInteractStations || !this.canInteractSigns || !this.canInteractSwitches || !this.canInteractFurniture;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addIntArray("allowedTeams", this.allowedTeamIDs.stream().mapToInt(Integer::intValue).toArray());
        save.addLong("ownerAuth", this.ownerAuth);
        save.addUnsafeString("ownerName", this.ownerName);
        save.addBoolean("allowOwnerTeam", this.allowOwnerTeam);
        save.addBoolean("canBreak", this.canBreak);
        save.addBoolean("canPlace", this.canPlace);
        save.addBoolean("canInteractDoors", this.canInteractDoors);
        save.addBoolean("canInteractContainers", this.canInteractContainers);
        save.addBoolean("canInteractStations", this.canInteractStations);
        save.addBoolean("canInteractSigns", this.canInteractSigns);
        save.addBoolean("canInteractSwitches", this.canInteractSwitches);
        save.addBoolean("canInteractFurniture", this.canInteractFurniture);
    }

    @Override
    public void applyLoadData(LoadData save) {
        int[] teams;
        super.applyLoadData(save);
        this.allowedTeamIDs.clear();
        for (int teamID : teams = save.getIntArray("allowedTeams", new int[0], false)) {
            this.allowedTeamIDs.add(teamID);
        }
        this.ownerAuth = save.getLong("ownerAuth", -1L);
        this.ownerName = save.getUnsafeString("ownerName", "");
        this.allowOwnerTeam = save.getBoolean("allowOwnerTeam", true);
        this.canBreak = save.getBoolean("canBreak", false);
        this.canPlace = save.getBoolean("canPlace", false);
        this.canInteractDoors = save.getBoolean("canInteractDoors", false);
        this.canInteractContainers = save.getBoolean("canInteractContainers", false);
        this.canInteractStations = save.getBoolean("canInteractStations", false);
        this.canInteractSigns = save.getBoolean("canInteractSigns", false);
        this.canInteractSwitches = save.getBoolean("canInteractSwitches", false);
        this.canInteractFurniture = save.getBoolean("canInteractFurniture", false);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        writer.putNextInt(this.allowedTeamIDs.size());
        for (int teamID : this.allowedTeamIDs) {
            writer.putNextInt(teamID);
        }
        writer.putNextLong(this.ownerAuth);
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

    @Override
    public void readPacket(PacketReader reader) {
        super.readPacket(reader);
        this.allowedTeamIDs.clear();
        int teamCount = reader.getNextInt();
        for (int i = 0; i < teamCount; ++i) {
            this.allowedTeamIDs.add(reader.getNextInt());
        }
        this.ownerAuth = reader.getNextLong();
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

