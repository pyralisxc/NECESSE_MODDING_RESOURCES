/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;

public class EnchantableSlot
extends ContainerSlot {
    public EnchantableSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (item == null) {
            return null;
        }
        if (!item.item.isEnchantable(item)) {
            String isEnchantableError = item.item.getIsEnchantableError(item);
            return isEnchantableError == null ? "" : isEnchantableError;
        }
        return null;
    }
}

