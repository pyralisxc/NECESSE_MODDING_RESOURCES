/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;

public class CloudContainerSlot
extends ContainerSlot {
    public final int itemID;

    public CloudContainerSlot(Inventory inventory, int inventorySlot, int itemID) {
        super(inventory, inventorySlot);
        this.itemID = itemID;
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (item != null && item.item.getID() == this.itemID) {
            return "";
        }
        return super.getItemInvalidError(item);
    }
}

