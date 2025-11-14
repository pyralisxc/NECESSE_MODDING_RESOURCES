/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public interface ItemDropperHandler {
    public void dropItem(InventoryItem var1, PlayerInventorySlot var2, boolean var3);
}

