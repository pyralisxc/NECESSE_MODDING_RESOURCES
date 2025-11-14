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

public class IntParameterHandler
extends ParameterHandler<Integer> {
    private Integer defaultValue;

    public IntParameterHandler() {
        this.defaultValue = 0;
    }

    public IntParameterHandler(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return Collections.emptyList();
    }

    @Override
    public Integer parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        try {
            return Integer.parseInt(arg);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException((arg.isEmpty() ? "Argument" : arg) + " for <" + parameter.name + "> is not a number");
        }
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        if (arg.isEmpty()) {
            return true;
        }
        try {
            Integer.parseInt(arg);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Integer getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

