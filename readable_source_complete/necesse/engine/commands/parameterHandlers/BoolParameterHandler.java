/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class BoolParameterHandler
extends ParameterHandler<Boolean> {
    public static String[] validTrue = new String[]{"1", "true"};
    public static String[] validFalse = new String[]{"0", "false"};
    private Boolean defaultValue;

    public BoolParameterHandler() {
        this.defaultValue = false;
    }

    public BoolParameterHandler(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return BoolParameterHandler.autocompleteFromArray(GameUtils.concat(validTrue, validFalse), null, null, argument);
    }

    @Override
    public Boolean parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        for (String s : validTrue) {
            if (!arg.equalsIgnoreCase(s)) continue;
            return true;
        }
        for (String s : validFalse) {
            if (!arg.equalsIgnoreCase(s)) continue;
            return false;
        }
        throw new IllegalArgumentException((arg.isEmpty() ? "Argument" : arg) + " for <" + parameter.name + "> must be either " + GameUtils.join(GameUtils.concat(validTrue, validFalse), ", ", " or "));
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public Boolean getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

