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
import necesse.engine.network.gameNetworkData.GNDItemShort;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class GNDShortParameterHandler
extends GNDItemParameterHandler<GNDItemShort> {
    public GNDShortParameterHandler() {
        super("short");
    }

    @Override
    protected List<AutoComplete> autocompleteSecondArg(CmdArgument argument) {
        return Collections.emptyList();
    }

    @Override
    protected GNDItemShort parseSecondArg(String arg, CmdParameter parameter) {
        try {
            return new GNDItemShort(Short.parseShort(arg));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(arg + " for <" + parameter.name + "> is not a short");
        }
    }

    @Override
    public GNDItemShort getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return null;
    }
}

