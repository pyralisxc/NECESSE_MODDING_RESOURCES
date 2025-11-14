/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers.gnd;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.gnd.GNDItemParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemDouble;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class GNDDoubleParameterHandler
extends GNDItemParameterHandler<GNDItemDouble> {
    public GNDDoubleParameterHandler() {
        super("double");
    }

    @Override
    protected List<AutoComplete> autocompleteSecondArg(CmdArgument argument) {
        return Collections.emptyList();
    }

    @Override
    protected GNDItemDouble parseSecondArg(String arg, CmdParameter parameter) {
        try {
            return new GNDItemDouble(Double.parseDouble(arg));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(arg + " for <" + parameter.name + "> is not a double");
        }
    }

    @Override
    public GNDItemDouble getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }
}

