/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComputedFunction;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.objectEntity.LadderDownObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;

public class LadderUpObjectEntity
extends PortalObjectEntity {
    private final int ladderDownID;
    private final GameSprite mapSprite;

    public LadderUpObjectEntity(String type, Level level, int x, int y, LevelIdentifier destination, int ladderDownID, GameSprite mapSprite) {
        super(level, type, x, y, destination, x, y);
        this.ladderDownID = ladderDownID;
        this.mapSprite = mapSprite;
    }

    @Override
    public void use(Server server, ServerClient client) {
        Level level2;
        GameMessage error;
        ComputedFunction<Level, GameMessage> isBlockingExit = new ComputedFunction<Level, GameMessage>(level -> {
            level.regionManager.ensureTilesAreLoaded(this.destinationTileX, this.destinationTileY, this.destinationTileX, this.destinationTileY);
            if (level.getObjectID(this.destinationTileX, this.destinationTileY) != this.ladderDownID) {
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
            if (level.getObjectID(this.destinationTileX, this.destinationTileY) != this.ladderDownID) {
                LadderDownObjectEntity.clearAndPlaceLadder(server, level, this.destinationTileX, this.destinationTileY, this.ladderDownID, true);
            }
            client.newStats.ladders_used.increment(1);
            this.runClearMobs((Level)level, this.destinationTileX, this.destinationTileY);
            return true;
        }, true);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-12, -12, 24, 24);
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        this.mapSprite.initDraw().size(24, 24).draw(x - 12, y - 12);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getObject().getDisplayName());
    }
}

