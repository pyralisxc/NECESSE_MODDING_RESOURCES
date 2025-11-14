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

public class WhisperHelpServerCommand
extends ModularChatCommand {
    public WhisperHelpServerCommand() {
        super("whisperhelp", "Whisper a message to another player", PermissionLevel.USER, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("message", new RestStringParameterHandler()));
    }

    @Override
    public boolean onlyForHelp() {
        return true;
    }

    @Override
    public String getFullHelp(boolean includeSlash) {
        String slash = includeSlash ? "/" : "";
        return slash + "w, " + slash + "whisper or " + slash + "pm " + this.getUsage();
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
    }
}

