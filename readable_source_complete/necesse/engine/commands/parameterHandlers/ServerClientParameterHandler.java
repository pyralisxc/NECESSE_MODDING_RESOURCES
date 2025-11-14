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

public class ServerClientParameterHandler
extends ParameterHandler<ServerClient> {
    public final boolean returnDefaultSelf;
    public final boolean searchAuthentication;

    public ServerClientParameterHandler() {
        this.searchAuthentication = false;
        this.returnDefaultSelf = false;
    }

    public ServerClientParameterHandler(boolean returnDefaultSelf, boolean searchAuthentication) {
        this.returnDefaultSelf = returnDefaultSelf;
        this.searchAuthentication = searchAuthentication;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        if (client != null) {
            return ServerClientParameterHandler.autocompleteFromList(client.streamClients().collect(Collectors.toList()), c -> true, ClientClient::getName, argument);
        }
        if (server != null) {
            return ServerClientParameterHandler.autocompleteFromList(server.streamClients().collect(Collectors.toList()), c -> true, ServerClient::getName, argument);
        }
        return Collections.emptyList();
    }

    @Override
    public ServerClient parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        ServerClient out;
        if (this.searchAuthentication) {
            try {
                long auth = Long.parseLong(arg);
                out = server.getClientByAuth(auth);
                if (out != null) {
                    return out;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if ((out = (ServerClient)server.streamClients().filter(sc -> sc.getName().toLowerCase().equals(arg.toLowerCase())).findFirst().orElse(null)) == null) {
            out = server.streamClients().filter(sc -> sc.getName().toLowerCase().contains(arg.toLowerCase())).findFirst().orElse(null);
        }
        if (out == null) {
            throw new IllegalArgumentException("Could not find player with name \"" + arg + "\" for <" + parameter.name + ">");
        }
        return out;
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        if (this.searchAuthentication) {
            try {
                long auth = Long.parseLong(arg);
                if (server != null && server.getClientByAuth(auth) != null) {
                    return true;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public ServerClient getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.returnDefaultSelf ? serverClient : null;
    }
}

