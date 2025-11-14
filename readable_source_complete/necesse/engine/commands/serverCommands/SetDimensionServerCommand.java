/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RelativeIntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class SetDimensionServerCommand
extends ModularChatCommand {
    public SetDimensionServerCommand() {
        super("setdimension", "Changes the dimension of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("dimension", new RelativeIntParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        if (player == null) {
            logs.add("Must specify <player>");
            return;
        }
        LevelIdentifier playerLevel = player.getLevelIdentifier();
        if (!playerLevel.isIslandPosition()) {
            logs.add(player.getName() + " is not on an island");
            return;
        }
        int dimension = RelativeIntParameterHandler.handleRelativeInt(args[1], playerLevel.getIslandDimension());
        player.changeIsland(playerLevel.getIslandX(), playerLevel.getIslandY(), dimension);
        logs.add("Set " + player.getName() + " dimension to " + dimension);
    }
}

