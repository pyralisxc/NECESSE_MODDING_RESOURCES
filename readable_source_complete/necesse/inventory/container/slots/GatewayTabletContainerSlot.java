/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.Level;

public class GatewayTabletContainerSlot
extends ContainerSlot {
    public GatewayTabletContainerSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        String superInvalid = super.getItemInvalidError(item);
        if (superInvalid != null) {
            return superInvalid;
        }
        if (item != null && !(item.item instanceof GatewayTabletItem)) {
            return "";
        }
        return null;
    }

    @Override
    public ItemCombineResult combineSlots(Level level, PlayerMob player, ContainerSlot slot, int amount, boolean copyLocked, boolean combineIsNew, String purpose) {
        if (slot.isClear()) {
            return ItemCombineResult.failure();
        }
        InventoryItem item = this.getItem();
        if (item != null) {
            if (player != null) {
                if (this.getItemInvalidError(slot.getItem()) != null) {
                    return ItemCombineResult.failure(this.getItemInvalidError(slot.getItem()));
                }
                player.getInv().addItemsDropRemaining(item, "addback", player, false, false, true);
                this.setItem(slot.getItem());
                slot.setItem(null);
                this.markDirty();
                slot.markDirty();
                return ItemCombineResult.success();
            }
            return super.combineSlots(level, player, slot, amount, copyLocked, combineIsNew, purpose);
        }
        return super.combineSlots(level, player, slot, amount, copyLocked, combineIsNew, purpose);
    }
}

