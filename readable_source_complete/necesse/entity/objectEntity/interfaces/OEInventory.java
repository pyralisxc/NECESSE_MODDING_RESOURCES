/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity.interfaces;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketOEInventoryUpdate;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.server.Server;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;

public interface OEInventory {
    default public void triggerInteracted() {
    }

    public Inventory getInventory();

    public GameMessage getInventoryName();

    default public void setInventoryName(String name) {
    }

    default public boolean canSetInventoryName() {
        return false;
    }

    default public boolean canQuickStackInventory() {
        return true;
    }

    default public boolean canRestockInventory() {
        return true;
    }

    default public boolean canSortInventory() {
        return true;
    }

    default public boolean canUseForNearbyCrafting() {
        return true;
    }

    default public InventoryRange getSettlementStorage() {
        Inventory inventory = this.getInventory();
        if (inventory != null) {
            return new InventoryRange(inventory);
        }
        return null;
    }

    default public SettlementRequestOptions getSettlementFuelRequestOptions() {
        return null;
    }

    default public InventoryRange getSettlementFuelInventoryRange() {
        return null;
    }

    default public boolean isSettlementStorageItemDisabled(Item item) {
        return false;
    }

    default public void setupDefaultSettlementStorage(SettlementInventory inventory) {
    }

    default public void serverTickInventorySync(Server server, ObjectEntity ent) {
        if (server == null) {
            return;
        }
        Inventory inventory = this.getInventory();
        if (inventory.isDirty()) {
            if (inventory.isFullDirty()) {
                server.network.sendToClientsWithEntity(new PacketObjectEntity(ent), ent);
                inventory.clean();
            } else {
                for (int i = 0; i < inventory.getSize(); ++i) {
                    if (!inventory.isDirty(i)) continue;
                    server.network.sendToClientsWithEntity(new PacketOEInventoryUpdate(this, i), ent);
                    inventory.clean(i);
                }
            }
        }
    }
}

