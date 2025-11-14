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

public class SetTeamServerCommand
extends ModularChatCommand {
    public SetTeamServerCommand() {
        super("setteam", "Sets the team of the player.", PermissionLevel.ADMIN, false, new CmdParameter("player", new StoredPlayerParameterHandler()), new CmdParameter("team", new TeamParameterHandler()));
    }

    @Override
    public boolean autocompleteOnServer() {
        return true;
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        StoredPlayerParameterHandler.StoredPlayer player = (StoredPlayerParameterHandler.StoredPlayer)args[0];
        PlayerTeam desiredTeam = (PlayerTeam)args[1];
        if (player != null) {
            if (desiredTeam != null) {
                PlayerTeam oldTeam = server.world.getTeams().getPlayerTeam(player.authentication);
                if (oldTeam != null) {
                    PlayerTeam.removeMember(server, oldTeam, player.authentication, true);
                    logs.add("Removed " + player.name + " from old team: " + oldTeam.getName() + " (ID " + oldTeam.teamID + ")");
                }
                PlayerTeam.addMember(server, desiredTeam, player.authentication);
                logs.add("Added " + player.name + " to team: " + desiredTeam.getName() + " (ID " + desiredTeam.teamID + ")");
            } else {
                logs.add("Could not find team");
            }
        } else {
            logs.add("Could not find player");
        }
    }
}

