/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MultiParameterHandler
extends ParameterHandler<Object[]> {
    private ParameterHandler[] handlers;
    private int argsUsed;

    public MultiParameterHandler(ParameterHandler ... handlers) {
        this.handlers = handlers;
        this.argsUsed = handlers[0].getArgsUsed();
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        ArrayList<AutoComplete> out = new ArrayList<AutoComplete>();
        for (ParameterHandler h : this.handlers) {
            for (AutoComplete ac : h.autocomplete(client, server, serverClient, argument)) {
                if (!out.stream().noneMatch(o -> o.equals(ac))) continue;
                out.add(ac);
            }
        }
        return out;
    }

    @Override
    public Object[] parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        IllegalArgumentException outEx = null;
        Object[] out = new Object[this.handlers.length];
        boolean validOut = false;
        for (int i = 0; i < this.handlers.length; ++i) {
            try {
                out[i] = this.handlers[i].parse(client, server, serverClient, arg, parameter);
                validOut = true;
                continue;
            }
            catch (IllegalArgumentException e) {
                if (outEx == null) {
                    outEx = e;
                }
                out[i] = this.handlers[i].getDefault(client, server, serverClient, parameter);
            }
        }
        if (!validOut) {
            throw outEx;
        }
        return out;
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        for (ParameterHandler h : this.handlers) {
            if (!h.tryParse(client, server, serverClient, arg, parameter)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Object[] getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        Object[] out = new Object[this.handlers.length];
        for (int i = 0; i < this.handlers.length; ++i) {
            out[i] = this.handlers[i].getDefault(client, server, serverClient, parameter);
        }
        return out;
    }

    @Override
    public int getArgsUsed() {
        return this.argsUsed;
    }
}

