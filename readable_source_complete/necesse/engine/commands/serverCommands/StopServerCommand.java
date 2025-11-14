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

public class StopServerCommand
extends ModularChatCommand {
    public StopServerCommand(String name) {
        super(name, "Saves and stops the server", PermissionLevel.OWNER, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        server.stop(s -> Thread.currentThread().interrupt());
    }

    @Override
    public boolean shouldBeListed() {
        return false;
    }
}

