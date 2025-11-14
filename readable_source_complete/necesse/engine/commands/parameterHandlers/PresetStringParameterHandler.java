/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.List;
import java.util.stream.Stream;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class PresetStringParameterHandler
extends ParameterHandler<String> {
    private String[] presets;
    private String defaultValue;

    public PresetStringParameterHandler(boolean lastIsDefault, String ... presets) {
        this.defaultValue = null;
        if (lastIsDefault) {
            this.presets = new String[presets.length - 1];
            System.arraycopy(presets, 0, this.presets, 0, this.presets.length);
            this.defaultValue = presets[presets.length - 1];
        } else {
            this.presets = presets;
        }
    }

    public PresetStringParameterHandler(String ... presets) {
        this(false, presets);
    }

    public <R> PresetStringParameterHandler(Stream<R> rStream) {
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return PresetStringParameterHandler.autocompleteFromArray(this.presets, s -> true, s -> s, argument);
    }

    @Override
    public String parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        for (String preset : this.presets) {
            if (!preset.equalsIgnoreCase(arg)) continue;
            return preset;
        }
        throw new IllegalArgumentException("Missing either: " + GameUtils.join(this.presets, "/") + " for <" + parameter.name + ">");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public String getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

