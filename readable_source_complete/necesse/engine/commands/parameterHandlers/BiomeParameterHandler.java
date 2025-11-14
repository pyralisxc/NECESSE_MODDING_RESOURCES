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
import necesse.engine.registries.BiomeRegistry;
import necesse.level.maps.biomes.Biome;

public class BiomeParameterHandler
extends ParameterHandler<Biome> {
    private Biome defaultValue;

    public BiomeParameterHandler() {
        this.defaultValue = null;
    }

    public BiomeParameterHandler(Biome defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return BiomeParameterHandler.autocompleteFromList(BiomeRegistry.getBiomes(), b -> b != BiomeRegistry.UNKNOWN, Biome::getStringID, argument);
    }

    @Override
    public Biome parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        for (Biome b : BiomeRegistry.getBiomes()) {
            if (b == BiomeRegistry.UNKNOWN || !arg.equalsIgnoreCase(b.getStringID())) continue;
            return b;
        }
        throw new IllegalArgumentException("Could not find biome \"" + arg + "\" for <" + parameter.name + ">");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public Biome getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

