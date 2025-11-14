/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.UnbanParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class UnbanServerCommand
extends ModularChatCommand {
    public UnbanServerCommand() {
        super("unban", "Removes a ban", PermissionLevel.ADMIN, false, new CmdParameter("authentication/name", new UnbanParameterHandler()));
    }

    @Override
    public boolean autocompleteOnServer() {
        return true;
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String auth = (String)args[0];
        if (Settings.removeBanned(auth)) {
            logs.add(auth + " is no longer banned.");
        } else {
            logs.add(auth + " is not banned.");
        }
    }
}

