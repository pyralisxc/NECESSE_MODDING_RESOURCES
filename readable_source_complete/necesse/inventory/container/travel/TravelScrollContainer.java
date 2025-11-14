/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.travel;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.inventory.container.travel.IslandData;
import necesse.inventory.container.travel.TravelContainer;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class TravelScrollContainer
extends TravelContainer {
    public TravelScrollContainer(NetworkClient client, int uniqueSeed, Packet contentPacket) {
        super(client, uniqueSeed, contentPacket);
    }

    @Override
    public void travelTo(IslandData destination, int dimension) {
        if (this.client.isClient()) {
            throw new IllegalStateException("Cannot be called client side, send a request travel packet instead");
        }
        ServerClient serverClient = this.client.getServerClient();
        if (this.travelLevel.isIslandPosition()) {
            if (this.client.playerMob.getInv().removeItems(ItemRegistry.getItem("travelscroll"), 1, false, false, false, false, "travelscroll") > 0) {
                this.sendTeleportEvent(this.client.playerMob);
                int lastTileX = this.client.playerMob.getTileX();
                int lastTileY = this.client.playerMob.getTileY();
                int oldIslandX = this.travelLevel.getIslandX();
                int oldIslandY = this.travelLevel.getIslandY();
                int newIslandX = destination.islandX;
                int newIslandY = destination.islandY;
                serverClient.changeIsland(newIslandX, newIslandY, dimension, level -> {
                    Line2D.Float line;
                    IntersectionPoint hit;
                    int newTileX = lastTileX;
                    if (level.tileWidth > 0) {
                        newTileX = oldIslandX < newIslandX ? 5 : (oldIslandX > newIslandX ? level.tileWidth - 5 : level.limitTileXToBounds(lastTileX, 0, 5));
                    }
                    int newTileY = lastTileY;
                    if (level.tileHeight > 0) {
                        newTileY = oldIslandY < newIslandY ? 5 : (oldIslandY > newIslandY ? level.tileHeight - 5 : level.limitTileYToBounds(lastTileY, 0, 5));
                    }
                    if (level.tileWidth > 0 && level.tileHeight > 0 && (hit = GameUtils.castRayFirstHit(line = new Line2D.Float(newTileX * 32 + 16, newTileY * 32 + 16, (float)level.tileWidth / 2.0f * 32.0f + 16.0f, (float)level.tileHeight / 2.0f * 32.0f + 16.0f), 100.0, ray -> level.getCollisionPoint((List<LevelObjectHit>)level.getCollisions((Shape)ray, new CollisionFilter().allLandTiles()), (Line2D)ray, true))) != null && hit.target != null) {
                        return new Point(((LevelObjectHit)hit.target).tileX * 32 + 16, ((LevelObjectHit)hit.target).tileY * 32 + 16);
                    }
                    return new Point(newTileX * 32 + 16, newTileY * 32 + 16);
                }, true);
                serverClient.newStats.island_travels.increment(1);
                serverClient.closeContainer(false);
                this.client.playerMob.addBuff(new ActiveBuff("teleportsickness", (Mob)this.client.playerMob, 10.0f, null), true);
                this.sendTeleportEvent(this.client.playerMob);
            }
        } else {
            GameLog.warn.println(serverClient.getName() + " tried to use travel scroll from non island level");
            this.close();
        }
    }

    protected void sendTeleportEvent(Mob mob) {
        TeleportEvent event = new TeleportEvent(mob.getX(), mob.getY() + 5, mob.getUniqueID());
        mob.getLevel().getServer().network.sendToClientsWithTile(new PacketLevelEvent(event), mob.getLevel(), mob.getTileX(), mob.getTileY());
    }
}

