/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.inventory.InventoryItem;

public interface InventoryFilter {
    public boolean isItemValid(int var1, InventoryItem var2);

    default public int getItemStackLimit(int slot, InventoryItem item) {
        return item.itemStackSize();
    }
}

