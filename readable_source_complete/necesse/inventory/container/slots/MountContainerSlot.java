/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;

public class MountContainerSlot
extends ContainerSlot {
    public MountContainerSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (item == null || item.item.isMountItem()) {
            return null;
        }
        return "";
    }

    @Override
    public int getItemStackLimit(InventoryItem item) {
        return 1;
    }
}

