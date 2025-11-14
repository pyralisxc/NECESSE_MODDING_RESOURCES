/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.LevelStorage;

public class SettlementStorageRecord {
    public final LevelStorage storage;
    public final int inventorySlot;
    private InventoryItem item;
    public int itemAmount;

    public SettlementStorageRecord(LevelStorage storage, int inventorySlot, InventoryItem item, int itemAmount) {
        this.storage = storage;
        this.inventorySlot = inventorySlot;
        this.item = item;
        this.itemAmount = itemAmount;
    }

    public InventoryItem getItem() {
        return this.item;
    }
}

