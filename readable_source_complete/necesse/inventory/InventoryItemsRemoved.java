/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public abstract class InventoryItemsRemoved {
    public final Inventory inventory;
    public final int inventorySlot;
    public final InventoryItem invItem;
    public final int amount;

    public InventoryItemsRemoved(Inventory inventory, int inventorySlot, InventoryItem invItem, int amount) {
        this.inventory = inventory;
        this.inventorySlot = inventorySlot;
        this.invItem = invItem;
        this.amount = amount;
    }

    public abstract void revert();
}

