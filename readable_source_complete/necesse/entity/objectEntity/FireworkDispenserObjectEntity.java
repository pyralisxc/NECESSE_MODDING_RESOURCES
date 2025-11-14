/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.packet.PacketSpawnFirework;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;
import necesse.level.maps.Level;

public class FireworkDispenserObjectEntity
extends InventoryObjectEntity {
    public boolean onCooldown = false;

    public FireworkDispenserObjectEntity(Level level, int x, int y) {
        super(level, x, y, 10);
    }

    public void fire() {
        if (!this.isServer() || this.onCooldown) {
            return;
        }
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.isSlotClear(i)) continue;
            InventoryItem item = this.inventory.getItem(i);
            if (!(item.item instanceof FireworkPlaceableItem)) continue;
            if (this.isServer()) {
                this.getLevel().getServer().network.sendToClientsWithTile(new PacketSpawnFirework(this.getLevel(), this.tileX * 32 + 16, this.tileY * 32 + 16, GameRandom.globalRandom.getIntBetween(600, 700), GameRandom.globalRandom.getIntBetween(150, 250), item.getGndData(), GameRandom.globalRandom.nextInt()), this.getLevel(), this.tileX, this.tileY);
                this.inventory.setAmount(i, this.inventory.getAmount(i) - 1);
                if (this.inventory.getAmount(i) <= 0) {
                    this.inventory.clearSlot(i);
                }
            }
            this.onCooldown = true;
            break;
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.onCooldown = false;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.onCooldown = false;
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        if (item != null) {
            return item.item instanceof FireworkPlaceableItem;
        }
        return true;
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canUseForNearbyCrafting() {
        return false;
    }

    @Override
    public boolean canSetInventoryName() {
        return false;
    }
}

