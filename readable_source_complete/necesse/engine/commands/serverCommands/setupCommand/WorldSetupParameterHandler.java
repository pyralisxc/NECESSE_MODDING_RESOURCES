/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.serverCommands.setupCommand.DemoServerCommand;
import necesse.engine.commands.serverCommands.setupCommand.WorldSetup;
import necesse.engine.commands.serverCommands.setupCommand.WorldSetupEntry;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class WorldSetupParameterHandler
extends ParameterHandler<WorldSetupEntry> {
    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return WorldSetupParameterHandler.autocompleteFromArray(DemoServerCommand.setups.keySet().toArray(new String[0]), null, null, argument);
    }

    @Override
    public WorldSetupEntry parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        WorldSetup setup = DemoServerCommand.setups.get(arg);
        if (setup != null) {
            return new WorldSetupEntry(arg, setup);
        }
        throw new IllegalArgumentException("Could not find setup with name \"" + arg + "\" for <" + parameter.name + ">");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public WorldSetupEntry getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }
}

