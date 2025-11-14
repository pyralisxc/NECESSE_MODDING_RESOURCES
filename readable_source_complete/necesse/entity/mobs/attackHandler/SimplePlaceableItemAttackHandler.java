/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Line2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.SimplePlaceAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public abstract class SimplePlaceableItemAttackHandler
extends SimplePlaceAttackHandler {
    protected PlaceableItem placeableItem;

    public SimplePlaceableItemAttackHandler(PlayerMob player, ItemAttackSlot slot, int startLevelX, int startLevelY, int seed, PlaceableItem placeableItem, GNDItemMap attackContentMap) {
        super(player, slot, startLevelX, startLevelY, seed, null);
        this.placeableItem = placeableItem;
        if (attackContentMap != null) {
            this.runInitialPlace(attackContentMap);
        }
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return this.placeableItem.canPlace(level, x, y, player, playerPositionLine, item, mapContent);
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        return this.placeableItem.onPlace(level, x, y, player, seed, item, mapContent);
    }

    @Override
    protected int getPlaceCooldown() {
        InventoryItem item = this.slot.getItem();
        if (item != null) {
            return this.placeableItem.getAttackHandlerPlaceCooldown(item, this.attackerMob);
        }
        return 200;
    }
}

