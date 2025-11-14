/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.List;
import java.util.Locale;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class EnumParameterHandler<T extends Enum<T>>
extends ParameterHandler<T> {
    private T defaultValue;
    private T[] values;

    public EnumParameterHandler(T defaultValue, T ... values) {
        this.defaultValue = defaultValue;
        this.values = values;
    }

    public EnumParameterHandler(T ... values) {
        this(null, (Enum[])values);
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return EnumParameterHandler.autocompleteFromArray(this.values, v -> true, v -> v.name().toLowerCase(Locale.ENGLISH), argument);
    }

    @Override
    public T parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        for (T value : this.values) {
            if (!((Enum)value).name().equalsIgnoreCase(arg)) continue;
            return value;
        }
        throw new IllegalArgumentException("Missing value for <" + parameter.name + ">");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public T getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

