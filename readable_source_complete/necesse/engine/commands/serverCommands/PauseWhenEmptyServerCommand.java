/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PauseWhenEmptyServerCommand
extends ModularChatCommand {
    public PauseWhenEmptyServerCommand() {
        super("pausewhenempty", "Enable/disable pause when empty setting", PermissionLevel.ADMIN, false, new CmdParameter("0/1", new BoolParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Settings.pauseWhenEmpty = (Boolean)args[0];
        if (!server.isHosted() && !server.isSingleplayer()) {
            Settings.saveServerSettings();
        }
        logs.add("Pause when empty set to: " + Settings.pauseWhenEmpty);
    }
}

