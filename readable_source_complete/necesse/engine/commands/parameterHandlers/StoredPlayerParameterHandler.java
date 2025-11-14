/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.WorldFile;

public class StoredPlayerParameterHandler
extends ParameterHandler<StoredPlayer> {
    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        if (server != null) {
            List<AutoComplete> out = StoredPlayerParameterHandler.autocompleteFromCollection(server.usedNames.values(), v -> true, String::valueOf, argument);
            out.addAll(StoredPlayerParameterHandler.autocompleteFromSet(server.usedNames.keySet(), k -> true, String::valueOf, argument));
            return out;
        }
        return Collections.emptyList();
    }

    @Override
    public StoredPlayer parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        try {
            long argAuth = Long.parseLong(arg);
            if (server.usedNames.containsKey(argAuth)) {
                return new StoredPlayer(server, server.usedNames.get(argAuth), argAuth);
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        for (long auth : server.usedNames.keySet()) {
            String name = server.usedNames.get(auth);
            if (!name.equalsIgnoreCase(arg) && !String.valueOf(auth).equals(arg)) continue;
            return new StoredPlayer(server, name, auth);
        }
        throw new IllegalArgumentException("Could not find player with name/auth \"" + arg + "\"");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public StoredPlayer getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }

    public static class StoredPlayer {
        public long authentication;
        public String name;
        public WorldFile file;
        public WorldFile mapFile;

        public StoredPlayer(Server server, String name, long authentication) {
            this.name = name;
            this.authentication = authentication;
            this.file = server.world.fileSystem.getPlayerFile(authentication);
            this.mapFile = server.world.fileSystem.getMapPlayerFile(authentication);
        }
    }
}

