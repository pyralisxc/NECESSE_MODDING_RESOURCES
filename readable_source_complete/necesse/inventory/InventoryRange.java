/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.inventory.Inventory;

public class InventoryRange {
    public final Inventory inventory;
    public final int startSlot;
    public final int endSlot;

    public InventoryRange(Inventory inventory, int startSlot, int endSlot) {
        this.inventory = inventory;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    public InventoryRange(Inventory inventory) {
        this(inventory, 0, inventory.getSize() - 1);
    }
}

