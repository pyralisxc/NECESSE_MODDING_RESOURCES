/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventoryManager;

public class PlayerInventorySlot {
    public final int inventoryID;
    public final int slot;

    public PlayerInventorySlot(int inventoryID, int slot) {
        this.inventoryID = inventoryID;
        this.slot = slot;
    }

    public PlayerInventorySlot(PlayerInventory inventory, int slot) {
        this(inventory.getInventoryID(), slot);
    }

    public PlayerInventory getInv(PlayerInventoryManager manager) {
        return manager.getInventoryByID(this.inventoryID);
    }

    public void setItem(PlayerInventoryManager manager, InventoryItem item) {
        PlayerInventory inv = this.getInv(manager);
        if (inv != null) {
            if (this.slot >= inv.getSize()) {
                inv.changeSize(this.slot + 1);
            }
            inv.setItem(this.slot, item);
        }
    }

    public void setItem(PlayerInventoryManager manager, InventoryItem item, boolean overrideIsNew) {
        PlayerInventory inv = this.getInv(manager);
        if (inv != null) {
            if (this.slot >= inv.getSize()) {
                inv.changeSize(this.slot + 1);
            }
            inv.setItem(this.slot, item, overrideIsNew);
        }
    }

    public InventoryItem getItem(PlayerInventoryManager manager) {
        PlayerInventory inv = this.getInv(manager);
        if (inv != null) {
            return inv.getItem(this.slot);
        }
        return null;
    }

    public boolean isSlotClear(PlayerInventoryManager manager) {
        PlayerInventory inv = this.getInv(manager);
        return inv != null && inv.isSlotClear(this.slot);
    }

    public boolean isItemLocked(PlayerInventoryManager manager) {
        PlayerInventory inv = this.getInv(manager);
        return inv != null && inv.isItemLocked(this.slot);
    }

    public void markDirty(PlayerInventoryManager manager) {
        PlayerInventory inv = this.getInv(manager);
        if (inv != null) {
            inv.markDirty(this.slot);
        }
    }

    public boolean equals(PlayerInventorySlot o) {
        return o != null && o.inventoryID == this.inventoryID && o.slot == this.slot;
    }
}

