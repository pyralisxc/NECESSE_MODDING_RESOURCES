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
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class BuffParameterHandler
extends ParameterHandler<Buff> {
    private boolean allowPassive;
    private Buff defaultValue;

    public BuffParameterHandler(boolean allowPassive) {
        this.allowPassive = allowPassive;
        this.defaultValue = null;
    }

    public BuffParameterHandler(boolean allowPassive, Buff defaultValue) {
        this.allowPassive = allowPassive;
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return BuffParameterHandler.autocompleteFromList(BuffRegistry.getBuffs(), b -> !b.isPassive() || this.allowPassive, Buff::getStringID, argument);
    }

    @Override
    public Buff parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        for (Buff b : BuffRegistry.getBuffs()) {
            if (!arg.equalsIgnoreCase(b.getStringID())) continue;
            return b;
        }
        throw new IllegalArgumentException("Could not find buff \"" + arg + "\" for <" + parameter.name + ">");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public Buff getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

