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

public class SetIslandServerCommand
extends ModularChatCommand {
    public SetIslandServerCommand() {
        super("setisland", "Changes the island of the player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("islandX", new RelativeIntParameterHandler()), new CmdParameter("islandY", new RelativeIntParameterHandler()), new CmdParameter("dimension", new RelativeIntParameterHandler(0), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        if (player == null) {
            logs.add("Must specify <player>");
            return;
        }
        LevelIdentifier playerLevel = player.getLevelIdentifier();
        int islandX = RelativeIntParameterHandler.handleRelativeInt(args[1], playerLevel.isIslandPosition() ? playerLevel.getIslandX() : player.spawnLevelIdentifier.getIslandX());
        int islandY = RelativeIntParameterHandler.handleRelativeInt(args[2], playerLevel.isIslandPosition() ? playerLevel.getIslandY() : player.spawnLevelIdentifier.getIslandY());
        int dimension = RelativeIntParameterHandler.handleRelativeInt(args[3], playerLevel.isIslandPosition() ? playerLevel.getIslandDimension() : player.spawnLevelIdentifier.getIslandDimension());
        player.changeIsland(islandX, islandY, dimension);
        logs.add("Set " + player.getName() + " island to " + islandX + ", " + islandY + " dim " + dimension);
    }
}

