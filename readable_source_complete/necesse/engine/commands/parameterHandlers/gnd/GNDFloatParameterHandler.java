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
import necesse.engine.network.gameNetworkData.GNDItemFloat;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class GNDFloatParameterHandler
extends GNDItemParameterHandler<GNDItemFloat> {
    public GNDFloatParameterHandler() {
        super("float");
    }

    @Override
    protected List<AutoComplete> autocompleteSecondArg(CmdArgument argument) {
        return Collections.emptyList();
    }

    @Override
    protected GNDItemFloat parseSecondArg(String arg, CmdParameter parameter) {
        try {
            return new GNDItemFloat(Float.parseFloat(arg));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(arg + " for <" + parameter.name + "> is not a float");
        }
    }

    @Override
    public GNDItemFloat getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }
}

