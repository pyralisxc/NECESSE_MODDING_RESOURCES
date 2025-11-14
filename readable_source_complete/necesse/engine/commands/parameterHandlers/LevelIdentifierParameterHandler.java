/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.level.gameTile.GameTile;

public class LevelIdentifierParameterHandler
extends ParameterHandler<LevelIdentifier> {
    public static final ArrayList<LevelIdentifier> levelIdentifierAutocomplete = new ArrayList();
    private final LevelIdentifier defaultValue;

    public LevelIdentifierParameterHandler() {
        this.defaultValue = null;
    }

    public LevelIdentifierParameterHandler(LevelIdentifier defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        List<AutoComplete> autoComplete = LevelIdentifierParameterHandler.autocompleteFromList(levelIdentifierAutocomplete, null, l -> l.stringID, argument);
        if (GlobalData.isDevMode()) {
            autoComplete.addAll(LevelIdentifierParameterHandler.autocompleteFromArray((GameTile[])TileRegistry.streamTiles().toArray(GameTile[]::new), i -> true, t -> "flat_" + t.getStringID(), argument));
        }
        return autoComplete;
    }

    @Override
    public LevelIdentifier parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        try {
            return new LevelIdentifier(arg.replace("flat_", ""));
        }
        catch (InvalidLevelIdentifierException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public LevelIdentifier getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }

    static {
        levelIdentifierAutocomplete.add(LevelIdentifier.SURFACE_IDENTIFIER);
        levelIdentifierAutocomplete.add(LevelIdentifier.CAVE_IDENTIFIER);
        levelIdentifierAutocomplete.add(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
    }
}

