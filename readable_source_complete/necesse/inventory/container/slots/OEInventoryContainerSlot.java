/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.slots.ContainerSlot;

public class OEInventoryContainerSlot
extends ContainerSlot {
    public OEInventoryContainerSlot(OEInventory oeInventory, int inventorySlot) {
        super(oeInventory.getInventory(), inventorySlot);
    }
}

