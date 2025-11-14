/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.container.Container;
import necesse.level.maps.Level;

public class ContainerSlot {
    private Container container;
    private int containerIndex = -1;
    private final Inventory inventory;
    private final int inventorySlot;

    public ContainerSlot(Inventory inventory, int inventorySlot) {
        this.inventory = inventory;
        this.inventorySlot = inventorySlot;
    }

    public void init(Container container, int slotIndex) {
        if (this.containerIndex != -1 && this.containerIndex != slotIndex) {
            throw new IllegalStateException("Container index already set");
        }
        this.container = container;
        this.containerIndex = slotIndex;
    }

    public Container getContainer() {
        return this.container;
    }

    public int getContainerIndex() {
        return this.containerIndex;
    }

    public String getItemInvalidError(InventoryItem item) {
        if (this.getInventory().isItemValid(this.inventorySlot, item)) {
            return null;
        }
        return "";
    }

    public int getItemStackLimit(InventoryItem item) {
        return this.getInventory().getItemStackLimit(this.inventorySlot, item);
    }

    public int getInventorySlot() {
        return this.inventorySlot;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public boolean isClear() {
        return this.getInventory().isSlotClear(this.getInventorySlot());
    }

    public InventoryItem getItem() {
        return this.getInventory().getItem(this.getInventorySlot());
    }

    public int getItemAmount() {
        if (this.isClear()) {
            return 0;
        }
        return this.getItem().getAmount();
    }

    public void setItem(InventoryItem item) {
        this.getInventory().setItem(this.getInventorySlot(), item);
        this.markDirty();
    }

    public void setAmount(int amount) {
        if (this.isClear()) {
            return;
        }
        this.getInventory().setAmount(this.getInventorySlot(), amount);
        this.markDirty();
    }

    public void setItemLocked(boolean locked) {
        if (this.isClear()) {
            return;
        }
        this.getInventory().setItemLocked(this.getInventorySlot(), locked);
    }

    public boolean isItemLocked() {
        return !this.isClear() && this.getInventory().isItemLocked(this.getInventorySlot());
    }

    public ItemCombineResult combineSlots(Level level, PlayerMob player, ContainerSlot slot, int amount, boolean copyLocked, boolean combineIsNew, String purpose) {
        ItemCombineResult out;
        if (slot.isClear()) {
            return ItemCombineResult.failure();
        }
        int maxAmount = Math.min(amount, slot.getItemAmount());
        InventoryItem current = this.getItem();
        if (current == null || !current.item.ignoreCombineStackLimit(level, player, current, slot.getItem(), purpose)) {
            maxAmount = Math.min(maxAmount, this.getItemStackLimit(slot.getItem()) - this.getItemAmount());
        }
        if (maxAmount <= 0) {
            return ItemCombineResult.failure();
        }
        InventoryItem moveCopy = slot.getItem().copy(maxAmount, copyLocked && slot.getItem().isLocked());
        String invalidError = this.getItemInvalidError(moveCopy);
        if (invalidError != null) {
            return ItemCombineResult.failure(invalidError);
        }
        if (this.isClear()) {
            out = ItemCombineResult.success();
            this.setItem(moveCopy);
            slot.setAmount(slot.getItemAmount() - maxAmount);
        } else {
            out = this.getInventory().combineItem(level, player, this.getInventorySlot(), slot.getItem(), amount, combineIsNew, purpose, null);
            if (out.success) {
                slot.getInventory().updateSlot(slot.getInventorySlot());
            }
        }
        if (slot.getItemAmount() <= 0) {
            slot.setItem(null);
        }
        if (out.success) {
            this.markDirty();
            slot.markDirty();
        }
        return out;
    }

    public ItemCombineResult combineSlots(Level level, PlayerMob player, ContainerSlot slot, boolean copyLocked, boolean combineIsNew, String purpose) {
        return this.combineSlots(level, player, slot, slot.getItemAmount(), copyLocked, false, purpose);
    }

    public ItemCombineResult swapItems(ContainerSlot slot) {
        String otherInvalidError = slot.getItemInvalidError(this.getItem());
        if (otherInvalidError != null) {
            return ItemCombineResult.failure(otherInvalidError);
        }
        if (slot.getItemStackLimit(this.getItem()) < this.getItemAmount()) {
            return ItemCombineResult.failure();
        }
        String myInvalidError = this.getItemInvalidError(slot.getItem());
        if (myInvalidError != null) {
            return ItemCombineResult.failure(myInvalidError);
        }
        if (this.getItemStackLimit(slot.getItem()) < slot.getItemAmount()) {
            return ItemCombineResult.failure();
        }
        InventoryItem tempItem = this.getItem();
        this.setItem(slot.getItem());
        slot.setItem(tempItem);
        return ItemCombineResult.success();
    }

    public boolean canLockItem() {
        return this.getInventory().canLockItem(this.getInventorySlot());
    }

    public void markDirty() {
        this.getInventory().markDirty(this.getInventorySlot());
    }

    public boolean isDirty() {
        return this.getInventory().isDirty(this.getInventorySlot());
    }
}

