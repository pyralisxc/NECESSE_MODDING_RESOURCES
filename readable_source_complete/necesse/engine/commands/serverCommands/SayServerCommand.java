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

public class SayServerCommand
extends ModularChatCommand {
    public SayServerCommand() {
        super("say", "Talks in the chat as Server", PermissionLevel.MODERATOR, false, new CmdParameter("message", new RestStringParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String msg = (String)args[0];
        String totalMsg = "Server: " + msg;
        server.network.sendToAllClients(new PacketChatMessage(totalMsg));
        logs.addConsole("(Say): " + totalMsg);
    }
}

