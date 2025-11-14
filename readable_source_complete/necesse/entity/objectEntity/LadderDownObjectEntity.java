/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.ComputedFunction;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class LadderDownObjectEntity
extends PortalObjectEntity {
    private final int ladderDownID;
    private final int ladderUpID;

    public LadderDownObjectEntity(String type, Level level, int x, int y, LevelIdentifier destinationLevelIdentifier, int ladderDownID, int ladderUpID) {
        super(level, type, x, y, level.getIdentifier(), x, y);
        this.destinationIdentifier = destinationLevelIdentifier;
        this.ladderDownID = ladderDownID;
        this.ladderUpID = ladderUpID;
    }

    @Override
    public void use(Server server, ServerClient client) {
        Level level2;
        GameMessage error;
        ComputedFunction<Level, GameMessage> isBlockingExit = new ComputedFunction<Level, GameMessage>(level -> {
            level.regionManager.ensureTilesAreLoaded(this.destinationTileX, this.destinationTileY, this.destinationTileX, this.destinationTileY);
            if (level.getObjectID(this.destinationTileX, this.destinationTileY) != this.ladderUpID) {
                return level.preventsLadderPlacement(this.destinationTileX, this.destinationTileY);
            }
            return null;
        });
        if (server.world.levelManager.isLoaded(this.getDestinationIdentifier()) && (error = isBlockingExit.get(level2 = server.world.getLevel(this.getDestinationIdentifier()))) != null) {
            client.sendChatMessage(error);
            return;
        }
        this.teleportClientToAroundDestination(client, level -> {
            GameMessage error;
            if (!isBlockingExit.isComputed() && (error = (GameMessage)isBlockingExit.get((Level)level)) != null) {
                client.sendChatMessage(error);
                return false;
            }
            level.regionManager.ensureTileIsLoaded(this.destinationTileX, this.destinationTileY);
            if (level.getObjectID(this.destinationTileX, this.destinationTileY) != this.ladderUpID) {
                LadderDownObjectEntity.clearAndPlaceLadder(server, level, this.destinationTileX, this.destinationTileY, this.ladderUpID, true);
            }
            client.newStats.ladders_used.increment(1);
            this.runClearMobs((Level)level, this.destinationTileX, this.destinationTileY);
            return true;
        }, true);
    }

    public static void clearAndPlaceLadder(Server server, Level level, int tileX, int tileY, int ladderObjectID, boolean clearLiquid) {
        GameObject ladderObject = ObjectRegistry.getObject(ladderObjectID);
        for (int i = -1; i <= 1; ++i) {
            int currentTileX = tileX + i;
            for (int j = -1; j <= 1; ++j) {
                int currentTileY = tileY + j;
                level.regionManager.ensureTileIsLoaded(currentTileX, currentTileY);
                GameObject obj = level.getObject(currentTileX, currentTileY);
                boolean shouldClearObject = obj.isClearedOnLadderPlacement(level, currentTileX, currentTileY);
                if (i == 0 && j == 0) {
                    if (!shouldClearObject) {
                        level.entityManager.doObjectDamageOverride(0, currentTileX, currentTileY, obj.objectHealth);
                    }
                    ladderObject.placeObject(level, currentTileX, currentTileY, 0, false);
                    if (level.getTile((int)currentTileX, (int)currentTileY).isLiquid) {
                        level.setTile(currentTileX, currentTileY, TileRegistry.dirtID);
                    }
                    server.network.sendToClientsWithTile(new PacketChangeObject(level, 0, currentTileX, currentTileY, ladderObjectID), level, currentTileX, currentTileY);
                    continue;
                }
                if (shouldClearObject && obj.preventsLadderPlacement(level, currentTileX, currentTileY) == null) {
                    level.setObject(currentTileX, currentTileY, 0);
                    server.network.sendToClientsWithTile(new PacketChangeObject(level, 0, currentTileX, currentTileY, 0), level, currentTileX, currentTileY);
                }
                if (!clearLiquid || !level.getTile((int)currentTileX, (int)currentTileY).isLiquid) continue;
                level.sendTileChangePacket(server, currentTileX, currentTileY, TileRegistry.dirtID);
            }
        }
    }
}

