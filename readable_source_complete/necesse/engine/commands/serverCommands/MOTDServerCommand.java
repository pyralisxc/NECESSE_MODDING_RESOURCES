/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MOTDServerCommand
extends ModularChatCommand {
    public MOTDServerCommand() {
        super("motd", "Sets or clears the message of the day. Use \\n for new line", PermissionLevel.ADMIN, false, new CmdParameter("clear/get/message", new RestStringParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String message = (String)args[0];
        if ((message = message.trim()).equals("clear")) {
            Settings.serverMOTD = "";
            logs.add("Cleared message of the day");
            if (!server.isHosted() && !server.isSingleplayer()) {
                Settings.saveServerSettings();
            }
        } else if (message.equals("get")) {
            if (Settings.serverMOTD.isEmpty()) {
                logs.add("There are currently no message of the day set");
            } else {
                GameMessageBuilder builder = new GameMessageBuilder().append("misc", "motd").append("\n").append(Settings.serverMOTD);
                logs.add(builder);
            }
        } else {
            Settings.serverMOTD = message = message.replace("\\n", "\n");
            logs.add("Set message of the day to:");
            logs.add(message);
            if (!server.isHosted() && !server.isSingleplayer()) {
                Settings.saveServerSettings();
            }
        }
    }
}

