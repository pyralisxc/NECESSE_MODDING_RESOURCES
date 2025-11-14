/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.commands.serverCommands.setupCommand.FindAndBuildArenaCustom;
import necesse.engine.commands.serverCommands.setupCommand.WorldSetup;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class FindBiomeAndBuildArena
extends FindAndBuildArenaCustom {
    public FindBiomeAndBuildArena(int size, int dimension, String ... biomeIDs) {
        super(size, dimension, biomeIDs);
    }

    @Override
    public void buildArena(Server server, ServerClient client, Level level, int tileX, int tileY, int size) {
        WorldSetup.buildRandomArena(level, GameRandom.globalRandom, tileX, tileY, size - 5, size + 5);
    }
}

