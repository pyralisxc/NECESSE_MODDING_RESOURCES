/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StoredPlayerParameterHandler;
import necesse.engine.commands.parameterHandlers.TeamParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class SetTeamOwnerServerCommand
extends ModularChatCommand {
    public SetTeamOwnerServerCommand() {
        super("setteamowner", "Sets the owner of the team. The new owner must be part of the team already", PermissionLevel.ADMIN, false, new CmdParameter("team", new TeamParameterHandler()), new CmdParameter("player", new StoredPlayerParameterHandler()));
    }

    @Override
    public boolean autocompleteOnServer() {
        return true;
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        PlayerTeam team = (PlayerTeam)args[0];
        StoredPlayerParameterHandler.StoredPlayer player = (StoredPlayerParameterHandler.StoredPlayer)args[1];
        if (team != null) {
            if (player != null) {
                if (team.isMember(player.authentication)) {
                    if (team.getOwner() != player.authentication) {
                        PlayerTeam.changeOwner(server, team, player.authentication);
                        logs.add("Made " + player.name + " the owner of team: " + team.getName() + " (ID " + team.teamID + ")");
                    } else {
                        logs.add(player.name + " is already owner of team: " + team.getName() + " (ID " + team.teamID + ")");
                    }
                } else {
                    logs.add(player.name + " is not part of team: " + team.getName() + " (ID " + team.teamID + ")");
                }
            } else {
                logs.add("Could not find player");
            }
        } else {
            logs.add("Could not find team");
        }
    }
}

