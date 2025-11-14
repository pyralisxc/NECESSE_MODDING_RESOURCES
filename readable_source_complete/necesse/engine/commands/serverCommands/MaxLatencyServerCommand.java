/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MaxLatencyServerCommand
extends ModularChatCommand {
    public MaxLatencyServerCommand() {
        super("maxlatency", "Sets the max latency before client timeout", PermissionLevel.ADMIN, false, new CmdParameter("seconds", new IntParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int seconds = (Integer)args[0];
        if (seconds < 1 || seconds > 300) {
            logs.add("Max latency must be between 1 and 300 seconds");
            return;
        }
        Settings.maxClientLatencySeconds = seconds;
        if (!server.isHosted() && !server.isSingleplayer()) {
            Settings.saveServerSettings();
        }
        logs.add("Max latency set to: " + Settings.maxClientLatencySeconds);
    }
}

