/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ClientClientParameterHandler
extends ParameterHandler<ClientClient> {
    public final boolean returnDefaultSelf;

    public ClientClientParameterHandler(boolean returnDefaultSelf) {
        this.returnDefaultSelf = returnDefaultSelf;
    }

    public ClientClientParameterHandler() {
        this(false);
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        if (client != null) {
            return ClientClientParameterHandler.autocompleteFromList(client.streamClients().collect(Collectors.toList()), c -> true, ClientClient::getName, argument);
        }
        if (server != null) {
            return ClientClientParameterHandler.autocompleteFromList(server.streamClients().collect(Collectors.toList()), c -> true, ServerClient::getName, argument);
        }
        return Collections.emptyList();
    }

    @Override
    public ClientClient parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        ClientClient out = client.streamClients().filter(sc -> sc.getName().toLowerCase().equals(arg.toLowerCase())).findFirst().orElse(null);
        if (out == null) {
            out = client.streamClients().filter(sc -> sc.getName().toLowerCase().contains(arg.toLowerCase())).findFirst().orElse(null);
        }
        if (out == null) {
            throw new IllegalArgumentException("Could not find player with name \"" + arg + "\" for <" + parameter.name + ">");
        }
        return out;
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public ClientClient getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.returnDefaultSelf ? client.getClient() : null;
    }
}

