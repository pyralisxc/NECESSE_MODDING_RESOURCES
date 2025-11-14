/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.HashMapSet;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;

public class TeamManager {
    private final Server server;
    private int nextTeamID = 0;
    private final HashMap<Integer, PlayerTeam> teams = new HashMap();
    final HashMap<Long, Integer> authToTeamID = new HashMap();
    final HashMapSet<Long, Integer> authToTeamIDJoinRequests = new HashMapSet();

    public TeamManager(Server server) {
        this.server = server;
    }

    public void cleanupEmptyTeams() {
        HashSet removes = new HashSet();
        this.teams.forEach((teamID, team) -> {
            if (!team.hasMembers()) {
                removes.add(teamID);
            }
        });
        removes.forEach(this.teams::remove);
    }

    public void addSaveData(SaveData save) {
        for (Map.Entry<Integer, PlayerTeam> e : this.teams.entrySet()) {
            PlayerTeam team = e.getValue();
            if (!team.hasMembers()) continue;
            SaveData teamSave = new SaveData("TEAM");
            team.addSaveData(teamSave);
            save.addSaveData(teamSave);
        }
    }

    public void applySaveData(LoadData save) {
        this.teams.clear();
        for (LoadData teamSave : save.getLoadDataByName("TEAM")) {
            try {
                PlayerTeam team = new PlayerTeam(this, this.getNextTeamID(), teamSave);
                this.teams.put(team.teamID, team);
                for (Long auth : team.getMembers()) {
                    PlayerTeam lastTeam;
                    int lastTeamID = this.authToTeamID.getOrDefault(auth, -1);
                    if (lastTeamID != -1 && (lastTeam = this.getTeam(lastTeamID)) != null) {
                        GameLog.warn.println(auth + " was part of multiple teams. Removed from old team ID " + lastTeam.teamID + ": " + lastTeam.getName());
                        lastTeam.removeMember(auth);
                    }
                    this.authToTeamID.put(auth, team.teamID);
                }
            }
            catch (Exception e) {
                GameLog.warn.println("Error loading player team");
                e.printStackTrace(GameLog.warn);
            }
        }
    }

    public void clearJoinRequests(long auth) {
        HashSet teams = (HashSet)this.authToTeamIDJoinRequests.clear(auth);
        if (teams != null) {
            for (Integer teamID : teams) {
                PlayerTeam team = this.getTeam(teamID);
                if (team == null) continue;
                team.joinRequests.remove(auth);
                new PvPJoinRequestUpdateEvent(auth, false, null).applyAndSendToClients(this.server, c -> c.getTeamID() == team.teamID);
            }
        }
    }

    private int getNextTeamID() {
        int next = this.nextTeamID++;
        if (this.nextTeamID < 0) {
            this.nextTeamID = 0;
        }
        if (this.teams.containsKey(next)) {
            return this.getNextTeamID();
        }
        return next;
    }

    public PlayerTeam getTeam(int teamID) {
        return this.teams.get(teamID);
    }

    public int getPlayerTeamID(long auth) {
        return this.authToTeamID.getOrDefault(auth, -1);
    }

    public PlayerTeam getPlayerTeam(long auth) {
        int teamID = this.getPlayerTeamID(auth);
        if (teamID == -1) {
            return null;
        }
        return this.getTeam(teamID);
    }

    public PlayerTeam createNewTeam(ServerClient owner) {
        PlayerTeam oldTeam = owner.getPlayerTeam();
        if (oldTeam != null) {
            PlayerTeam.removeMember(this.server, oldTeam, owner.authentication, false);
        }
        PlayerTeam newTeam = new PlayerTeam(this, this.getNextTeamID(), owner);
        this.teams.put(newTeam.teamID, newTeam);
        for (Long joinRequest : owner.joinRequests) {
            newTeam.addJoinRequest(joinRequest);
        }
        owner.joinRequests.clear();
        PlayerTeam.addMember(this.server, newTeam, owner.authentication);
        return newTeam;
    }

    public Iterable<PlayerTeam> getTeams() {
        return this.teams.values();
    }

    public Stream<PlayerTeam> streamTeams() {
        return this.teams.values().stream();
    }
}

