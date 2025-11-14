/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Point;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public interface RespawnObject {
    public boolean canRespawn(Level var1, int var2, int var3, ServerClient var4);

    default public boolean isCurrentSpawn(Level level, int tileX, int tileY, ServerClient client) {
        return client.spawnLevelIdentifier.equals(level.getIdentifier()) && client.spawnTile.x == tileX && client.spawnTile.y == tileY;
    }

    default public boolean removeSpawn(Level level, int tileX, int tileY, ServerClient client, boolean sendChatMessage) {
        if (this.isCurrentSpawn(level, tileX, tileY, client)) {
            client.resetSpawnPoint(level.getServer());
            if (sendChatMessage) {
                client.sendChatMessage(new LocalMessage("misc", "spawnremoved"));
            }
            return true;
        }
        return false;
    }

    default public void setSpawn(Level level, int tileX, int tileY, ServerClient client, boolean sendChatMessage) {
        if (client.achievementsLoaded()) {
            client.achievements().SET_SPAWN.markCompleted(client);
        }
        client.spawnLevelIdentifier = level.getIdentifier();
        client.spawnTile = new Point(tileX, tileY);
        if (sendChatMessage) {
            client.sendChatMessage(new LocalMessage("misc", "spawnset"));
        }
    }

    public Point getSpawnOffset(Level var1, int var2, int var3, ServerClient var4);

    public static Point calculateSpawnOffset(Level level, int tileX, int tileY, ServerClient client) {
        if (level == null) {
            return null;
        }
        level.regionManager.ensureTilesAreLoaded(tileX - 2, tileY - 2, tileX + 2, tileY + 2);
        GameObject spawnObject = level.getObject(tileX, tileY);
        if (spawnObject instanceof RespawnObject) {
            if (!((RespawnObject)((Object)spawnObject)).canRespawn(level, tileX, tileY, client)) {
                return null;
            }
            return ((RespawnObject)((Object)spawnObject)).getSpawnOffset(level, tileX, tileY, client);
        }
        return null;
    }
}

