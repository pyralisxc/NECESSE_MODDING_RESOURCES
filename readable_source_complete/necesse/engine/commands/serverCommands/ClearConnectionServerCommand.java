/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.InvalidNetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ClearConnectionServerCommand
extends ModularChatCommand {
    public ClearConnectionServerCommand() {
        super("clearconnection", "Clears the connection info of a client, making them try to reconnect to session", PermissionLevel.OWNER, true, new CmdParameter("player", new ServerClientParameterHandler(true, false)));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        player.networkInfo = new InvalidNetworkInfo();
        logs.add("Cleared connection info of " + player.getName());
    }
}

