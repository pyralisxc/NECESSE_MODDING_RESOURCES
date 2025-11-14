/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class CmdNameParameterHandler
extends ParameterHandler<ChatCommand> {
    private boolean ignoreOnlyHelpCommands;

    public CmdNameParameterHandler(boolean ignoreOnlyHelpCommands) {
        this.ignoreOnlyHelpCommands = ignoreOnlyHelpCommands;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        if (client != null) {
            return CmdNameParameterHandler.autocompleteFromList(client.commandsManager.getCommands(), cmd -> !cmd.onlyForHelp() && cmd.havePermissions(client, server, serverClient), cmd -> cmd.name, argument);
        }
        if (server != null) {
            return CmdNameParameterHandler.autocompleteFromList(server.commandsManager.getServerCommands(), cmd -> !cmd.onlyForHelp() && cmd.havePermissions(client, server, serverClient), cmd -> cmd.name, argument);
        }
        return Collections.emptyList();
    }

    @Override
    public ChatCommand parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        ChatCommand cmd = null;
        List<ChatCommand> commands = client != null ? client.commandsManager.getCommands() : server.commandsManager.getServerCommands();
        for (ChatCommand c : commands) {
            if (c.onlyForHelp() && this.ignoreOnlyHelpCommands || !c.name.equalsIgnoreCase(arg)) continue;
            cmd = c;
            break;
        }
        if (cmd != null && cmd.onlyForHelp() && this.ignoreOnlyHelpCommands) {
            cmd = null;
        }
        if (cmd == null) {
            throw new IllegalArgumentException("Could not find command \"" + arg + "\" for <" + parameter.name + ">");
        }
        return cmd;
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public ChatCommand getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }
}

