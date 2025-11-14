/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MeServerCommand
extends ModularChatCommand {
    public MeServerCommand() {
        super("me", "Declare an action to the entire server", PermissionLevel.USER, false, new CmdParameter("action", new RestStringParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (serverClient == null) {
            logs.add("Command cannot be run from server.");
        } else if (args[0] == null || ((String)args[0]).length() == 0) {
            logs.add("Missing action");
        } else {
            String message = serverClient.getName() + " " + String.valueOf(args[0]);
            server.network.sendToAllClients(new PacketChatMessage(message));
        }
    }
}

