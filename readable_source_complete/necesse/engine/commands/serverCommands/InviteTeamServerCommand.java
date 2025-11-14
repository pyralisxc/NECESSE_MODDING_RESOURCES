/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class InviteTeamServerCommand
extends ModularChatCommand {
    public InviteTeamServerCommand() {
        super("invite", "Invites a player to your team", PermissionLevel.USER, false, new CmdParameter("player", new ServerClientParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (serverClient == null) {
            logs.add("Command cannot be run from server.");
            return;
        }
        ServerClient target = (ServerClient)args[0];
        if (target != null) {
            if (serverClient == target) {
                logs.add("Cannot invite yourself");
            } else {
                PlayerTeam team = serverClient.getPlayerTeam();
                if (team == null) {
                    team = server.world.getTeams().createNewTeam(serverClient);
                    logs.add(new LocalMessage("ui", "teamcreated", "name", team.getName()));
                }
                logs.add(new LocalMessage("ui", "teaminvited", "name", target.getName(), "team", team.getName()));
                PlayerTeam.invitePlayer(server, team, target);
            }
        } else {
            logs.add("Could not find player");
        }
    }
}

