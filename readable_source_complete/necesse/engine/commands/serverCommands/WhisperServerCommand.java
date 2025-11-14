/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class WhisperServerCommand
extends ModularChatCommand {
    public WhisperServerCommand(String name) {
        super(name, "Whisper a message to another player", PermissionLevel.USER, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("message", new RestStringParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        if (target == null || target == serverClient) {
            logs.add("Cannot whisper self");
            return;
        }
        String message = (String)args[1];
        String clientName = serverClient == null ? "Server" : serverClient.getName();
        logs.add("To " + target.getName() + ": " + message);
        logs.addClient("From " + clientName + ": " + message, target);
    }

    @Override
    public boolean shouldBeListed() {
        return false;
    }
}

