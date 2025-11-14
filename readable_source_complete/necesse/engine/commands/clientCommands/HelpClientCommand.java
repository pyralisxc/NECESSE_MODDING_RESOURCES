/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import java.util.ArrayList;
import java.util.Collections;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.CmdNameParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.gfx.GameColor;

public class HelpClientCommand
extends ModularChatCommand {
    public HelpClientCommand() {
        super("help", "Lists all commands or gives information about a specific command", PermissionLevel.USER, false, new CmdParameter("page/command", new MultiParameterHandler(new IntParameterHandler(1), new CmdNameParameterHandler(true)), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Object[] subArgs = (Object[])args[0];
        int page = (Integer)subArgs[0];
        ChatCommand cmd = (ChatCommand)subArgs[1];
        if (cmd != null) {
            if (cmd.havePermissions(client, server, serverClient)) {
                logs.add(GameColor.CYAN.getColorCode() + "Command \"" + cmd.getName() + "\":");
                logs.add("Permission level: " + cmd.permissionLevel.name.translate());
                logs.add(cmd.getFullUsage(true));
                logs.add(cmd.getFullAction());
            } else {
                logs.add(GameColor.RED.getColorCode() + "You do not have permissions for command: " + cmd.getName());
            }
        } else {
            ArrayList<ChatCommand> sorted = new ArrayList<ChatCommand>(client.commandsManager.getCommands());
            sorted.removeIf(c -> !c.shouldBeListed() || !c.havePermissions(client, server, serverClient));
            Collections.sort(sorted);
            int commandsPerPage = 5;
            int maxPages = sorted.size() / commandsPerPage + 1;
            page = GameMath.limit(page, 1, maxPages);
            int startIndex = GameMath.limit(page - 1, 0, maxPages) * commandsPerPage;
            int endIndex = Math.min(sorted.size(), startIndex + commandsPerPage);
            logs.add(GameColor.CYAN.getColorCode() + "Commands page " + page + " of " + maxPages + " (" + client.getPermissionLevel().name.translate() + "):");
            for (int i = startIndex; i < endIndex; ++i) {
                ChatCommand c2 = sorted.get(i);
                logs.add(c2.getFullHelp(true));
            }
        }
    }

    @Override
    public boolean shouldBeListed() {
        return false;
    }
}

