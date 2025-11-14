/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.CommandsManager;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MyPermissionsServerCommand
extends ModularChatCommand {
    public MyPermissionsServerCommand() {
        super("mypermissions", "Shows your permission level", PermissionLevel.USER, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        logs.add("Your permission level: " + CommandsManager.getPermissionLevel((Client)client, (ServerClient)serverClient).name.translate());
        if (serverClient != null) {
            serverClient.setPermissionLevel(serverClient.getPermissionLevel(), false);
        }
    }
}

