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
import necesse.engine.util.GameUtils;

public class PlaytimeServerCommand
extends ModularChatCommand {
    public PlaytimeServerCommand() {
        super("playtime", "Shows your current playtime on the server", PermissionLevel.USER, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (serverClient == null) {
            logs.add("Command cannot be run from server.");
        } else {
            logs.add("Total playtime: " + GameUtils.formatSeconds(serverClient.characterStats().time_played.get()));
            logs.add("Current session: " + GameUtils.formatSeconds(serverClient.getSessionTime()));
        }
    }
}

