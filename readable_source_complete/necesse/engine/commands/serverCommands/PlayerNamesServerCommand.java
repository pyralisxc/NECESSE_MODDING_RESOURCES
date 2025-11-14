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

public class PlayerNamesServerCommand
extends ModularChatCommand {
    public PlayerNamesServerCommand() {
        super("playernames", "Lists all authentications and their names", PermissionLevel.MODERATOR, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        logs.add("Total players stored: " + server.usedNames.size());
        for (long auth : server.usedNames.keySet()) {
            logs.add(auth + " - " + server.usedNames.get(auth));
        }
    }
}

