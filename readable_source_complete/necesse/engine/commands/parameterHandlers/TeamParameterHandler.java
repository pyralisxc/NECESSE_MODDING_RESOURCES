/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.engine.team.TeamManager;

public class TeamParameterHandler
extends ParameterHandler<PlayerTeam> {
    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        if (server != null) {
            TeamManager teamManager = server.world.getTeams();
            return TeamParameterHandler.autocompleteFromArray((PlayerTeam[])teamManager.streamTeams().filter(PlayerTeam::hasMembers).toArray(PlayerTeam[]::new), t -> true, PlayerTeam::getName, argument);
        }
        return Collections.emptyList();
    }

    @Override
    public PlayerTeam parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        try {
            int argTeamID = Integer.parseInt(arg);
            PlayerTeam team = server.world.getTeams().getTeam(argTeamID);
            if (team != null) {
                return team;
            }
        }
        catch (NumberFormatException argTeamID) {
            // empty catch block
        }
        PlayerTeam team = server.world.getTeams().streamTeams().filter(PlayerTeam::hasMembers).filter(t -> t.getName().equals(arg)).findFirst().orElse(null);
        if (team != null) {
            return team;
        }
        throw new IllegalArgumentException("Could not find player team with name/ID \"" + arg + "\"");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        try {
            Integer.parseInt(arg);
            return true;
        }
        catch (NumberFormatException e) {
            return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
        }
    }

    @Override
    public PlayerTeam getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }
}

