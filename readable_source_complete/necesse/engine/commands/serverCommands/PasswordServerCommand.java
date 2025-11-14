/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PasswordServerCommand
extends ModularChatCommand {
    public PasswordServerCommand() {
        super("password", "Set a password of the server, blank will be no password", PermissionLevel.OWNER, false, new CmdParameter("password", new StringParameterHandler(), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String password = (String)args[0];
        if (password != null && password.length() != 0) {
            Settings.serverPassword = password;
            logs.add("Password set to: " + Settings.serverPassword);
        } else {
            Settings.serverPassword = "";
            logs.add("Removed password from server");
        }
        if (!server.isHosted() && !server.isSingleplayer()) {
            Settings.saveServerSettings();
        }
        server.getSettings().password = Settings.serverPassword;
    }
}

