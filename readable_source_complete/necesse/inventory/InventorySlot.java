/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class InventorySlot {
    public final Inventory inventory;
    public final int slot;

    public InventorySlot(Inventory inventory, int slot) {
        this.inventory = inventory;
        this.slot = slot;
    }

    public void clean() {
        this.inventory.clean(this.slot);
    }

    public void markDirty() {
        this.inventory.markDirty(this.slot);
    }

    public boolean isDirty() {
        return this.inventory.isDirty(this.slot);
    }

    public void clearSlot() {
        this.inventory.clearSlot(this.slot);
    }

    public boolean isSlotClear() {
        return this.inventory.isSlotClear(this.slot);
    }

    public void setItem(InventoryItem item) {
        this.inventory.setItem(this.slot, item);
    }

    public void setItem(InventoryItem item, boolean overrideIsNew) {
        this.inventory.setItem(this.slot, item, overrideIsNew);
    }

    public InventoryItem getItem() {
        return this.inventory.getItem(this.slot);
    }

    public String getItemStringID() {
        return this.inventory.getItemStringID(this.slot);
    }

    public int getItemID() {
        return this.inventory.getItemID(this.slot);
    }

    public void setAmount(int amount) {
        this.inventory.setAmount(this.slot, amount);
    }

    public void addAmount(int amount) {
        this.inventory.addAmount(this.slot, amount);
    }

    public int getAmount() {
        return this.inventory.getAmount(this.slot);
    }

    public Item getItemSlot() {
        return this.inventory.getItemSlot(this.slot);
    }

    public boolean canLockItem() {
        return this.inventory.canLockItem(this.slot);
    }

    public boolean isItemLocked() {
        return this.inventory.isItemLocked(this.slot);
    }

    public void setItemLocked(boolean locked) {
        this.inventory.setItemLocked(this.slot, locked);
    }

    public final boolean isItemValid(InventoryItem item) {
        return this.inventory.isItemValid(this.slot, item);
    }

    public final int getItemStackLimit(InventoryItem item) {
        return this.inventory.getItemStackLimit(this.slot, item);
    }
}

