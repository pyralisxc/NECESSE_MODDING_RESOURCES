/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class StopCreditsClientCommand
extends ModularChatCommand {
    public StopCreditsClientCommand() {
        super("stopcredits", "Stops the credits drawing", PermissionLevel.USER, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (client.stopCreditsDraw()) {
            logs.add("Stopped credits");
        } else {
            logs.add("No credits to stop");
        }
    }
}

