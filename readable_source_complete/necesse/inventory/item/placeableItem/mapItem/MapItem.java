/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.mapItem;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class MapItem
extends PlaceableItem {
    public MapItem(int stackSize) {
        super(stackSize, true);
        this.dropsAsMatDeathPenalty = true;
        this.keyWords.add("map");
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }
}

