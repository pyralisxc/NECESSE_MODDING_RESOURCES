/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;

public abstract class SettlementStoragePickupFuture {
    public final LevelStorage storage;
    public final InventoryItem item;

    public SettlementStoragePickupFuture(LevelStorage storage, InventoryItem item) {
        this.storage = storage;
        this.item = item;
    }

    public abstract SettlementStoragePickupSlot accept(int var1);
}

