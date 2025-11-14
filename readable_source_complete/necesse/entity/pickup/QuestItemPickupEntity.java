/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.pickup;

import necesse.engine.network.server.ServerClient;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class QuestItemPickupEntity
extends ItemPickupEntity {
    public QuestItemPickupEntity() {
    }

    public QuestItemPickupEntity(Level level, InventoryItem item, float x, float y, float dx, float dy) {
        super(level, item, x, y, dx, dy);
    }

    @Override
    public void onPickup(ServerClient client) {
        if (client.playerMob.getInv().getAmount(this.item.item, false, false, true, true, "questdrop") <= 0) {
            super.onPickup(client);
        } else {
            this.remove();
        }
    }

    @Override
    public boolean collidesWith(PickupEntity item) {
        return item.getID() != this.getID() && super.collidesWith(item);
    }
}

