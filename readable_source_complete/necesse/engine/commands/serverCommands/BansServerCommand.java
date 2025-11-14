/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class BansServerCommand
extends ModularChatCommand {
    public BansServerCommand() {
        super("bans", "Lists all current bans", PermissionLevel.ADMIN, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (Settings.banned.size() == 0) {
            logs.add("There are no listed bans.");
        } else {
            logs.add(Settings.banned.size() + " total bans:");
            for (String s : Settings.banned) {
                logs.add(s);
            }
        }
    }
}

