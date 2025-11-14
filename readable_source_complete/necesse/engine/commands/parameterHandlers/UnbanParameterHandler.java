/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.List;
import necesse.engine.Settings;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class UnbanParameterHandler
extends ParameterHandler<String> {
    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return UnbanParameterHandler.autocompleteFromList(Settings.banned, s -> true, s -> s, argument);
    }

    @Override
    public String parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        return arg;
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public String getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }
}

