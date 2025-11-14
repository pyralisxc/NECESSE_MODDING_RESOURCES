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

public class AllowCheatsServerCommand
extends ModularChatCommand {
    public AllowCheatsServerCommand() {
        super("allowcheats", "Enables/allows cheats on this world (NOT REVERSIBLE)", PermissionLevel.OWNER, true, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (server.world.settings.allowCheats) {
            logs.add("Cheats are already allowed");
        } else {
            server.world.settings.enableCheats();
            logs.add("Cheats are now allowed");
        }
    }
}

