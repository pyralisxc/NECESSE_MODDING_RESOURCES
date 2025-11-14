/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.engine.network.server.Server;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;

public abstract class PlayerTempInventory
extends PlayerInventory {
    private boolean isDisposed;

    public PlayerTempInventory(PlayerMob player, int size, int invID) {
        super(player, size, false, false, false);
        this.invID = invID;
    }

    public abstract boolean shouldDispose();

    public boolean isDisposed() {
        return this.isDisposed;
    }

    public void dispose() {
        this.isDisposed = true;
        if (this.player.isServer()) {
            Server server = this.player.getServer();
            for (int i = 0; i < this.getSize(); ++i) {
                if (this.isSlotClear(i)) continue;
                InventoryItem item = this.getItem(i);
                if (server.isStopped()) {
                    if (item.getAmount() > 0) {
                        this.player.getLevel().entityManager.pickups.add(item.getPickupEntity(this.player.getLevel(), this.player.x, this.player.y));
                    }
                } else {
                    this.player.getInv().addItemsDropRemaining(item, "addback", this.player, false, false);
                }
                this.clearSlot(i);
            }
        }
    }
}

