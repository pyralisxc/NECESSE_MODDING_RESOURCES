/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class InternalInventoryItemContainerSlot
extends ContainerSlot {
    private InternalInventoryItemInterface internalInventoryItemInterface;

    public InternalInventoryItemContainerSlot(Inventory inventory, int inventorySlot, InternalInventoryItemInterface internalInventoryItemInterface) {
        super(inventory, inventorySlot);
        this.internalInventoryItemInterface = internalInventoryItemInterface;
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (item == null || this.internalInventoryItemInterface.isValidItem(item)) {
            return null;
        }
        return "";
    }
}

