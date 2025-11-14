/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.Inventory
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.container.slots.InternalInventoryItemContainerSlot
 *  necesse.inventory.item.miscItem.InternalInventoryItemInterface
 */
package tomeofpower.containers.trinket;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.InternalInventoryItemContainerSlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import tomeofpower.config.TomeConfig;

public class LimitedEnchantmentSlot
extends InternalInventoryItemContainerSlot {
    public LimitedEnchantmentSlot(Inventory inventory, int slot, InternalInventoryItemInterface internalInventoryItemInterface) {
        super(inventory, slot, internalInventoryItemInterface);
    }

    public String getItemInvalidError(InventoryItem item) {
        String parentError = super.getItemInvalidError(item);
        if (parentError != null) {
            return parentError;
        }
        return null;
    }

    public int getItemStackLimit(InventoryItem item) {
        return TomeConfig.MAX_ENCHANTS_PER_SLOT;
    }
}

