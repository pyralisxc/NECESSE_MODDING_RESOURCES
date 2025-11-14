/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.level.maps.Level;

public class TrashContainerSlot
extends ContainerSlot {
    public TrashContainerSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public ItemCombineResult combineSlots(Level level, PlayerMob player, ContainerSlot slot, int amount, boolean copyLocked, boolean combineIsNew, String purpose) {
        if (slot.isClear()) {
            return ItemCombineResult.failure();
        }
        int amountLimit = Math.min(slot.getItemAmount(), amount);
        if (this.getItem() != null && this.getItem().canCombine(level, player, slot.getItem(), "trash")) {
            this.getItem().item.onCombine(level, player, this.getInventory(), this.getInventorySlot(), this.getItem(), slot.getItem(), Integer.MAX_VALUE, amountLimit, combineIsNew, "trash", null);
            this.setAmount(Math.min(this.getItemAmount(), this.getItemStackLimit(this.getItem())));
            if (slot.getItemAmount() <= 0) {
                slot.setItem(null);
            }
        } else {
            int actualAmount = Math.min(this.getItemStackLimit(slot.getItem()), amountLimit);
            this.setItem(slot.getItem().copy(actualAmount));
            slot.setAmount(slot.getItemAmount() - actualAmount);
            if (slot.getItemAmount() <= 0) {
                slot.setItem(null);
            }
        }
        this.markDirty();
        return ItemCombineResult.success();
    }
}

