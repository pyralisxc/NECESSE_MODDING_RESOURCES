/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Line2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketPlayerPlaceItem;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;

public class ObjectItemAttackHandler
extends PlaceItemAttackHandler<PlaceItemAttackHandler.PlacePosition> {
    protected ObjectItem objectItem;
    protected int placeDir;

    public ObjectItemAttackHandler(PlayerMob player, ItemAttackSlot slot, int startLevelX, int startLevelY, int seed, ObjectItem objectItem, GNDItemMap attackContentMap) {
        super(player, slot, startLevelX, startLevelY, seed);
        this.objectItem = objectItem;
        int n = this.placeDir = player.isAttacking ? player.beforeAttackDir : player.getDir();
        if (attackContentMap != null) {
            this.runInitialPlace(attackContentMap);
        }
    }

    protected void updatePlayerDir() {
        if (this.attackerMob.isAttacking) {
            this.attackerMob.beforeAttackDir = this.placeDir;
        } else {
            this.attackerMob.setDir(this.placeDir);
        }
    }

    @Override
    protected PlaceItemAttackHandler.PlacePosition placeItem(InventoryItem item, int currentX, int currentY, Line2D playerPositionLine, GNDItemMap mapContent) {
        Level level = this.attackerMob.getLevel();
        PlayerMob player = (PlayerMob)this.attackerMob;
        this.updatePlayerDir();
        if (mapContent == null) {
            mapContent = new GNDItemMap();
            ObjectPlaceOption placeOption = this.objectItem.getBestPlaceOption(level, currentX, currentY, item, (PlayerMob)this.attackerMob, null, true);
            if (placeOption != null) {
                this.objectItem.setupPlaceMapContent(mapContent, placeOption, level);
            }
        }
        String canPlaceError = this.objectItem.canPlace(level, currentX, currentY, player, playerPositionLine, item, mapContent);
        if (level.isServer() && player.isServerClient() && this.objectItem.shouldSendToOtherClients(level, currentX, currentY, player, item, canPlaceError, mapContent)) {
            ServerClient serverClient = player.getServerClient();
            level.getServer().network.sendToClientsWithEntityExcept(new PacketPlayerPlaceItem(level, serverClient, item, currentX, currentY, canPlaceError, mapContent), serverClient.playerMob, serverClient);
        }
        if (canPlaceError == null) {
            InventoryItem newItem = this.objectItem.onPlace(level, currentX, currentY, player, this.seed, item, mapContent);
            if (newItem.getAmount() <= 0) {
                this.slot.setItem(null);
            } else {
                this.slot.setItem(newItem);
            }
            return new PlaceItemAttackHandler.PlacePosition(currentX, currentY, mapContent);
        }
        this.objectItem.onAttemptPlace(level, currentX, currentY, player, item, mapContent, canPlaceError);
        return null;
    }

    @Override
    protected boolean placeServerItem(InventoryItem item, PlaceItemAttackHandler.PlacePosition placePosition, Line2D playerPositionLine) {
        Level level = this.attackerMob.getLevel();
        PlayerMob player = (PlayerMob)this.attackerMob;
        this.updatePlayerDir();
        String canPlaceError = this.objectItem.canPlace(level, placePosition.placeX, placePosition.placeY, player, playerPositionLine, item, placePosition.attackMapContent);
        if (level.isServer() && player.isServerClient() && this.objectItem.shouldSendToOtherClients(level, placePosition.placeX, placePosition.placeY, player, item, canPlaceError, placePosition.attackMapContent)) {
            ServerClient serverClient = player.getServerClient();
            level.getServer().network.sendToClientsWithEntityExcept(new PacketPlayerPlaceItem(level, serverClient, item, placePosition.placeX, placePosition.placeY, canPlaceError, placePosition.attackMapContent), serverClient.playerMob, serverClient);
        }
        if (canPlaceError == null) {
            InventoryItem newItem = this.objectItem.onPlace(level, placePosition.placeX, placePosition.placeY, player, this.seed, item, placePosition.attackMapContent);
            if (newItem.getAmount() <= 0) {
                this.slot.setItem(null);
            } else {
                this.slot.setItem(newItem);
            }
            return true;
        }
        this.objectItem.onAttemptPlace(level, placePosition.placeX, placePosition.placeY, player, item, placePosition.attackMapContent, canPlaceError);
        return false;
    }

    @Override
    protected void onServerPlaceInvalid(InventoryItem item, PlaceItemAttackHandler.PlacePosition placePosition, Line2D playerPositionLine) {
        Level level = this.attackerMob.getLevel();
        PlayerMob player = (PlayerMob)this.attackerMob;
        ServerClient client = player.getServerClient();
        ObjectPlaceOption placeOption = this.objectItem.getPlaceOptionFromMap(placePosition.attackMapContent);
        if (placeOption != null) {
            level.checkTileAndObjectsHashGNDMap(client, placePosition.attackMapContent, placeOption.tileX, placeOption.tileY, true);
        } else {
            int tileX = GameMath.getTileCoordinate(placePosition.placeX);
            int tileY = GameMath.getTileCoordinate(placePosition.placeY);
            level.checkTileAndObjectsHashGNDMap(client, placePosition.attackMapContent, tileX, tileY, true);
        }
    }

    @Override
    protected PlaceItemAttackHandler.PlacePosition createPlacePositionFromPacket(PacketReader reader) {
        return new PlaceItemAttackHandler.PlacePosition(reader);
    }

    @Override
    protected int getPlaceCooldown() {
        InventoryItem item = this.slot.getItem();
        if (item != null) {
            return this.objectItem.getAttackHandlerPlaceCooldown(item, this.attackerMob);
        }
        return 200;
    }
}

