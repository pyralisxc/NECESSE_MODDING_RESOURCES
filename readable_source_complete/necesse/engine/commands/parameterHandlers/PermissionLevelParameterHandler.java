/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PermissionLevelParameterHandler
extends ParameterHandler<PermissionLevel> {
    private PermissionLevel defaultValue;

    public PermissionLevelParameterHandler() {
        this.defaultValue = null;
    }

    public PermissionLevelParameterHandler(PermissionLevel defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return PermissionLevelParameterHandler.autocompleteFromArray(PermissionLevel.values(), null, e -> e.name().toLowerCase(), argument);
    }

    @Override
    public PermissionLevel parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        PermissionLevel[] levels;
        for (PermissionLevel level : levels = PermissionLevel.values()) {
            if (!arg.equalsIgnoreCase(level.toString())) continue;
            return level;
        }
        throw new IllegalArgumentException("Could not find permission level \"" + arg + "\" for <" + parameter.name + ">");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public PermissionLevel getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

