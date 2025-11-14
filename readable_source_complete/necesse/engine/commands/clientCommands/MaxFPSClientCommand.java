/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MaxFPSClientCommand
extends ModularChatCommand {
    public MaxFPSClientCommand() {
        super("maxfps", "Sets max fps", PermissionLevel.USER, false, new CmdParameter("fps", new IntParameterHandler(), false, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int fps;
        Settings.maxFPS = fps = ((Integer)args[0]).intValue();
        Settings.saveClientSettings();
        logs.add("Set max fps to " + fps);
    }
}

