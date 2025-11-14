/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import necesse.inventory.InventoryItem;

public class ItemUsed {
    public final boolean used;
    public final InventoryItem item;

    public ItemUsed(boolean used, InventoryItem item) {
        this.used = used;
        this.item = item;
    }
}

