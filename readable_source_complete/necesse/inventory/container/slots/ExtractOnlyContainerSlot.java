/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;

public class ExtractOnlyContainerSlot
extends ContainerSlot {
    public ExtractOnlyContainerSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        return "";
    }
}

