/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class StopHelpServerCommand
extends ModularChatCommand {
    public StopHelpServerCommand() {
        super("stophelp", "Saves and stops the server", PermissionLevel.OWNER, false, new CmdParameter[0]);
    }

    @Override
    public boolean onlyForHelp() {
        return true;
    }

    @Override
    public String getFullHelp(boolean includeSlash) {
        String slash = includeSlash ? "/" : "";
        return slash + "stop, " + slash + "exit or " + slash + "quit " + this.getUsage();
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
    }
}

