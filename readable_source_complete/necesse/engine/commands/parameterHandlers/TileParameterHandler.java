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
import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.GameTile;

public class TileParameterHandler
extends ParameterHandler<GameTile> {
    public int defaultValue = -1;

    public TileParameterHandler(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public TileParameterHandler() {
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return TileParameterHandler.autocompleteFromArray((GameTile[])TileRegistry.streamTiles().toArray(GameTile[]::new), i -> true, GameTile::getStringID, argument);
    }

    @Override
    public GameTile parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        GameTile out = TileRegistry.getTile(arg);
        if (out == null) {
            throw new IllegalArgumentException("Could not find tile with stringID \"" + arg + "\" for <" + parameter.name + ">");
        }
        return out;
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public GameTile getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        if (this.defaultValue == -1) {
            return null;
        }
        return TileRegistry.getTile(this.defaultValue);
    }
}

