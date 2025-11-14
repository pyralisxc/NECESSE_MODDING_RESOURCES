/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.team;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.network.packet.PacketPlayerJoinedTeam;
import necesse.engine.network.packet.PacketPlayerLeftTeam;
import necesse.engine.network.packet.PacketPlayerTeamInviteReceive;
import necesse.engine.network.packet.PacketPlayerTeamRequestReceive;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.team.TeamManager;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.inventory.container.teams.PvPCurrentTeamUpdateEvent;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;
import necesse.inventory.container.teams.PvPMemberUpdateEvent;
import necesse.inventory.container.teams.PvPOwnerUpdateEvent;
import necesse.inventory.container.teams.PvPPublicUpdateEvent;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PlayerTeam {
    public final TeamManager manager;
    public final int teamID;
    private String name;
    private long owner;
    private boolean isPublic = true;
    private final HashSet<Long> members = new HashSet();
    HashSet<Long> joinRequests = new HashSet();

    PlayerTeam(TeamManager manager, int teamID, ServerClient owner) {
        this.manager = manager;
        this.teamID = teamID;
        this.name = owner.getName() + "'s team";
        this.owner = owner.authentication;
        this.members.add(this.owner);
    }

    PlayerTeam(TeamManager manager, int teamID, LoadData save) {
        long[] members;
        this.manager = manager;
        this.teamID = teamID;
        this.name = save.getSafeString("name", "Unknown team");
        this.owner = save.getLong("owner", -1L);
        this.isPublic = save.getBoolean("isPublic", this.isPublic, false);
        for (long auth : members = save.getLongArray("members", new long[0])) {
            this.members.add(auth);
        }
    }

    public void addSaveData(SaveData save) {
        save.addInt("teamID", this.teamID);
        save.addSafeString("name", this.name);
        save.addLong("owner", this.owner);
        save.addBoolean("isPublic", this.isPublic);
        save.addLongArray("members", this.members.stream().filter(Objects::nonNull).mapToLong(Long::longValue).toArray());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasMembers() {
        return !this.members.isEmpty();
    }

    public int getMemberCount() {
        return this.members.size();
    }

    public boolean isMember(long auth) {
        return this.members.contains(auth);
    }

    void addMember(long auth) {
        this.manager.authToTeamID.put(auth, this.teamID);
        this.members.add(auth);
        if (this.owner == -1L) {
            this.owner = auth;
        }
        this.joinRequests.remove(auth);
        this.manager.clearJoinRequests(auth);
    }

    void removeMember(long auth) {
        block1: {
            this.manager.authToTeamID.remove(auth);
            this.members.remove(auth);
            if (this.owner != auth) break block1;
            this.owner = -1L;
            Iterator<Long> iterator = this.members.iterator();
            if (iterator.hasNext()) {
                long member;
                this.owner = member = iterator.next().longValue();
            }
        }
    }

    void addJoinRequest(long auth) {
        this.manager.authToTeamIDJoinRequests.add(auth, this.teamID);
        this.joinRequests.add(auth);
    }

    void removeJoinRequest(long auth) {
        ((HashSet)this.manager.authToTeamIDJoinRequests.get(auth)).remove(this.teamID);
        this.joinRequests.remove(auth);
    }

    public int getJoinRequestsCount() {
        return this.joinRequests.size();
    }

    public long getOwner() {
        return this.owner;
    }

    private void changeOwner(long owner) {
        this.owner = owner;
    }

    private void changePublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public Iterable<Long> getMembers() {
        return this.members;
    }

    public Stream<Long> streamMembers() {
        return this.members.stream();
    }

    public boolean hasJoinRequested(long auth) {
        return this.joinRequests.contains(auth);
    }

    public Iterable<Long> getJoinRequests() {
        return this.joinRequests;
    }

    public Stream<Long> streamJoinRequests() {
        return this.joinRequests.stream();
    }

    public Stream<ServerClient> streamOnlineMembers(Server server) {
        if (server == null) {
            return Stream.empty();
        }
        return this.streamMembers().map(server::getClientByAuth).filter(Objects::nonNull);
    }

    public static void addMember(Server server, PlayerTeam team, long auth) {
        PlayerTeam oldTeam = server.world.getTeams().getPlayerTeam(auth);
        if (oldTeam != null) {
            PlayerTeam.removeMember(server, oldTeam, auth, false);
        }
        ServerClient client = server.getClientByAuth(auth);
        team.addMember(auth);
        if (client != null) {
            client.setTeamID(team.teamID);
            client.joinRequests.clear();
            PvPTeamsContainer.sendSingleUpdate(client);
            SettlementsWorldData.getSettlementsData(server).updateOwnerTeamID(client.authentication, team.teamID);
        }
        PacketPlayerJoinedTeam packet = new PacketPlayerJoinedTeam(server, auth);
        new PvPMemberUpdateEvent(auth, true, packet.name).applyAndSendToClients(server, c -> c.getTeamID() == team.teamID);
        server.network.sendToAllClients(packet);
        PlayerTeam.onUpdateTeam(server, auth, team.teamID);
    }

    public static void removeMember(Server server, PlayerTeam team, long auth, boolean isKick) {
        ServerClient client = server.getClientByAuth(auth);
        boolean changeOwner = team.owner == auth;
        team.removeMember(auth);
        if (client != null) {
            client.setTeamID(-1);
            if (changeOwner) {
                PvPTeamsContainer.sendUpdates(client, team);
            } else {
                new PvPCurrentTeamUpdateEvent(client).applyAndSendToClient(client);
            }
        } else if (changeOwner) {
            PvPTeamsContainer.sendUpdates(server, team);
        }
        new PvPMemberUpdateEvent(auth, false, null).applyAndSendToClients(server, c -> c.getTeamID() == team.teamID);
        server.network.sendToAllClients(new PacketPlayerLeftTeam(server, auth, team, isKick));
        PlayerTeam.onUpdateTeam(server, auth, -1);
    }

    public static void addJoinRequest(Server server, PlayerTeam team, long auth) {
        String name = server.getNameByAuth(auth, "N/A");
        team.addJoinRequest(auth);
        new PvPJoinRequestUpdateEvent(auth, true, name).applyAndSendToClients(server, c -> c.getTeamID() == team.teamID);
        team.streamOnlineMembers(server).forEach(c -> c.sendPacket(new PacketPlayerTeamRequestReceive(auth, name)));
    }

    public static void removeJoinRequest(Server server, PlayerTeam team, long auth) {
        team.removeJoinRequest(auth);
        new PvPJoinRequestUpdateEvent(auth, false, null).applyAndSendToClients(server, c -> c.getTeamID() == team.teamID);
    }

    public static void changeOwner(Server server, PlayerTeam team, long newOwnerAuth) {
        team.changeOwner(newOwnerAuth);
        new PvPOwnerUpdateEvent(newOwnerAuth).applyAndSendToClients(server, c -> c.getTeamID() == team.teamID);
    }

    public static void changePublic(Server server, PlayerTeam team, boolean isPublic) {
        team.changePublic(isPublic);
        new PvPPublicUpdateEvent(isPublic).applyAndSendToClients(server, c -> c.getTeamID() == team.teamID);
    }

    public static void invitePlayer(Server server, PlayerTeam team, ServerClient target) {
        target.teamInvites.add(team.teamID);
        target.sendPacket(new PacketPlayerTeamInviteReceive(team.teamID, team.getName()));
        PvPTeamsContainer.sendSingleUpdate(target);
    }

    public static void onUpdateTeam(Server server, long auth, int teamID) {
        SettlementsWorldData.getSettlementsData(server).updateOwnerTeamID(auth, teamID);
    }
}

