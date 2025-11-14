/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StoredPlayerParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.engine.team.TeamManager;

public class GetTeamServerCommand
extends ModularChatCommand {
    public GetTeamServerCommand() {
        super("getteam", "Gets the current team of the player", PermissionLevel.ADMIN, false, new CmdParameter("player", new StoredPlayerParameterHandler()));
    }

    @Override
    public boolean autocompleteOnServer() {
        return true;
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        StoredPlayerParameterHandler.StoredPlayer player = (StoredPlayerParameterHandler.StoredPlayer)args[0];
        if (player != null) {
            ServerClient currentClient = server.getClientByAuth(player.authentication);
            if (currentClient != null) {
                PlayerTeam team = currentClient.getPlayerTeam();
                if (team == null) {
                    logs.add(currentClient.getName() + " is not part of any team");
                } else {
                    logs.add(currentClient.getName() + " is part of team: " + team.getName() + " (ID " + team.teamID + ")");
                }
            } else {
                TeamManager teamManager = server.world.getTeams();
                PlayerTeam team = teamManager.getPlayerTeam(player.authentication);
                if (team != null) {
                    logs.add(player.name + " is part of team: " + team.getName() + " (ID " + team.teamID + ")");
                } else {
                    logs.add(player.name + " is not part of any team");
                }
            }
        } else {
            logs.add("Could not find player");
        }
    }
}

