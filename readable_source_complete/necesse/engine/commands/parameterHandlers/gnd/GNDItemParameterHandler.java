/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers.gnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDBooleanParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDByteParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDDoubleParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDFloatParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDIntParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDLongParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDShortParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class GNDItemParameterHandler<T extends GNDItem>
extends ParameterHandler<GNDItem> {
    public static ArrayList<GNDItemParameterHandler<?>> itemParameterHandlers = new ArrayList();
    protected String prefix;

    public static MultiParameterHandler getMultiParameterHandler() {
        return new MultiParameterHandler(itemParameterHandlers.toArray(new GNDItemParameterHandler[0]));
    }

    public static GNDItem getReturnedItem(Object arg) {
        Object[] returns;
        for (Object o : returns = (Object[])arg) {
            if (o == null) continue;
            return (GNDItem)o;
        }
        return null;
    }

    public GNDItemParameterHandler(String prefix) {
        if (prefix.contains(":")) {
            throw new IllegalArgumentException("Prefix cannot contain semicolon");
        }
        this.prefix = prefix;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        String secondArg = this.getSecondArg(argument.arg);
        if (secondArg == null) {
            return GNDItemParameterHandler.autocompleteFromCollection(Collections.singleton(this.prefix + ":"), null, null, argument);
        }
        return this.autocompleteSecondArg(new CmdArgument(argument.param, secondArg, argument.argCount));
    }

    protected abstract List<AutoComplete> autocompleteSecondArg(CmdArgument var1);

    @Override
    public T parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        String secondArg = this.getSecondArg(arg);
        if (secondArg == null) {
            throw new IllegalArgumentException("Invalid argument for GND " + this.prefix + " arg \"" + arg + "\" for <" + parameter.name + ">");
        }
        if (secondArg.isEmpty()) {
            throw new IllegalArgumentException("Missing value GND " + this.prefix + " argument for <" + parameter.name + ">");
        }
        return this.parseSecondArg(secondArg, parameter);
    }

    protected abstract T parseSecondArg(String var1, CmdParameter var2);

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        if (arg.isEmpty()) {
            return true;
        }
        String secondArg = this.getSecondArg(arg);
        if (secondArg == null) {
            return !GNDItemParameterHandler.autocompleteFromCollection(Collections.singleton(this.prefix), null, null, new CmdArgument(parameter, arg, 1)).isEmpty();
        }
        if (secondArg.isEmpty()) {
            return true;
        }
        try {
            this.parseSecondArg(secondArg, parameter);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public abstract T getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4);

    protected String getSecondArg(String arg) {
        int index = arg.indexOf(58);
        if (index == -1) {
            return null;
        }
        if (!arg.substring(0, index).equals(this.prefix)) {
            return null;
        }
        return arg.substring(index + 1);
    }

    static {
        itemParameterHandlers.add(new GNDBooleanParameterHandler());
        itemParameterHandlers.add(new GNDByteParameterHandler());
        itemParameterHandlers.add(new GNDShortParameterHandler());
        itemParameterHandlers.add(new GNDIntParameterHandler());
        itemParameterHandlers.add(new GNDLongParameterHandler());
        itemParameterHandlers.add(new GNDFloatParameterHandler());
        itemParameterHandlers.add(new GNDDoubleParameterHandler());
        itemParameterHandlers.add(new GNDStringParameterHandler());
    }
}

