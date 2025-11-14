/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.awt.Point;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.serverCommands.setupCommand.WorldSetup;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.level.maps.Level;

public abstract class FindArenaWorldSetup
implements WorldSetup {
    public final int dimension;
    public final String[] biomeIDs;

    public FindArenaWorldSetup(int dimension, String ... biomeIDs) {
        this.dimension = dimension;
        this.biomeIDs = biomeIDs;
    }

    @Override
    public void apply(Server server, ServerClient client, boolean forceNew, CommandLog logs) {
        Point islandPos = WorldSetup.findClosestBiome(client, this.dimension, forceNew, this.biomeIDs);
        if (islandPos == null) {
            logs.add("Could not find a close biome");
            return;
        }
        client.changeIsland(islandPos.x, islandPos.y, this.dimension, level -> {
            Point tile = this.findArenaSpawnTile(server, client, (Level)level);
            if (tile != null) {
                if (level.getObjectID(tile.x, tile.y) == 0) {
                    return new Point(tile.x * 32 + 16, tile.y * 32 + 16);
                }
                Point teleportPos = PortalObjectEntity.getTeleportDestinationAroundObject(level, client.playerMob, tile.x, tile.y, true);
                if (teleportPos == null) {
                    teleportPos = new Point(tile.x * 32 + 16, tile.y * 32 + 16);
                }
                return teleportPos;
            }
            logs.add("Could not find arena location");
            return null;
        }, true);
    }

    public abstract Point findArenaSpawnTile(Server var1, ServerClient var2, Level var3);
}

