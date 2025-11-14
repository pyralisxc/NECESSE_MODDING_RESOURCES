/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Line2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public abstract class SimplePlaceAttackHandler
extends PlaceItemAttackHandler<PlaceItemAttackHandler.PlacePosition> {
    public SimplePlaceAttackHandler(PlayerMob player, ItemAttackSlot slot, int startLevelX, int startLevelY, int seed, GNDItemMap attackContentMap) {
        super(player, slot, startLevelX, startLevelY, seed);
        if (attackContentMap != null) {
            this.runInitialPlace(attackContentMap);
        }
    }

    @Override
    protected PlaceItemAttackHandler.PlacePosition placeItem(InventoryItem item, int currentX, int currentY, Line2D playerPositionLine, GNDItemMap mapContent) {
        Level level = this.attackerMob.getLevel();
        PlayerMob player = (PlayerMob)this.attackerMob;
        if (mapContent == null) {
            mapContent = new GNDItemMap();
            this.setupPlaceMap(mapContent, level, currentX, currentY, player, this.seed, item);
        }
        PlaceItemAttackHandler.PlacePosition placePosition = new PlaceItemAttackHandler.PlacePosition(currentX, currentY, mapContent);
        String canPlaceError = this.canPlace(level, currentX, currentY, player, playerPositionLine, item, mapContent);
        if (canPlaceError == null) {
            InventoryItem newItem = this.onPlace(level, currentX, currentY, player, this.seed, item, mapContent);
            if (newItem.getAmount() <= 0) {
                this.slot.setItem(null);
            } else {
                this.slot.setItem(newItem);
            }
            return placePosition;
        }
        return null;
    }

    @Override
    protected boolean placeServerItem(InventoryItem item, PlaceItemAttackHandler.PlacePosition placePosition, Line2D playerPositionLine) {
        PlayerMob player;
        Level level = this.attackerMob.getLevel();
        String canPlaceError = this.canPlace(level, placePosition.placeX, placePosition.placeY, player = (PlayerMob)this.attackerMob, playerPositionLine, item, placePosition.attackMapContent);
        if (canPlaceError == null) {
            InventoryItem newItem = this.onPlace(level, placePosition.placeX, placePosition.placeY, player, this.seed, item, placePosition.attackMapContent);
            if (newItem.getAmount() <= 0) {
                this.slot.setItem(null);
            } else {
                this.slot.setItem(newItem);
            }
            return true;
        }
        return false;
    }

    @Override
    protected PlaceItemAttackHandler.PlacePosition createPlacePositionFromPacket(PacketReader reader) {
        return new PlaceItemAttackHandler.PlacePosition(reader);
    }

    public void setupPlaceMap(GNDItemMap map, Level level, int x, int y, PlayerMob player, int seed, InventoryItem item) {
        item.item.setupAttackMapContent(map, level, x, y, player, seed, item);
    }

    public abstract String canPlace(Level var1, int var2, int var3, PlayerMob var4, Line2D var5, InventoryItem var6, GNDItemMap var7);

    public abstract InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, GNDItemMap var7);
}

