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

public class ClearTeamServerCommand
extends ModularChatCommand {
    public ClearTeamServerCommand() {
        super("clearteam", "Removes the player from his current team", PermissionLevel.ADMIN, false, new CmdParameter("player", new StoredPlayerParameterHandler()));
    }

    @Override
    public boolean autocompleteOnServer() {
        return true;
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        StoredPlayerParameterHandler.StoredPlayer player = (StoredPlayerParameterHandler.StoredPlayer)args[0];
        if (player != null) {
            PlayerTeam currentTeam = server.world.getTeams().getPlayerTeam(player.authentication);
            if (currentTeam != null) {
                PlayerTeam.removeMember(server, currentTeam, player.authentication, true);
                logs.add("Removed " + player.name + " from team: " + currentTeam.getName() + " (ID " + currentTeam.teamID + ")");
            } else {
                logs.add(player.name + " is not part of any team");
            }
        } else {
            logs.add("Could not find player");
        }
    }
}

