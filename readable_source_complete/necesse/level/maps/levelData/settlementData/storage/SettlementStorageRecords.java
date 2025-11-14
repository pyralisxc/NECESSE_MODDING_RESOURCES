/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import necesse.engine.registries.SettlementStorageIndexRegistry;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;

public class SettlementStorageRecords {
    protected Level level;
    public final SettlementStorageIndex[] indexes;

    public SettlementStorageRecords(Level level) {
        this.level = level;
        this.indexes = SettlementStorageIndexRegistry.getNewIndexesArray(level);
    }

    public <T extends SettlementStorageIndex> T getIndex(int indexID, Class<T> expectedClass) {
        return (T)((SettlementStorageIndex)expectedClass.cast(this.indexes[indexID]));
    }

    public <T extends SettlementStorageIndex> T getIndex(Class<T> indexClass) {
        return this.getIndex(SettlementStorageIndexRegistry.getIndexID(indexClass), indexClass);
    }

    public void clear() {
        for (SettlementStorageIndex index : this.indexes) {
            index.clear();
        }
    }

    public void add(InventoryItem inventoryItem, SettlementStorageRecord record) {
        for (SettlementStorageIndex index : this.indexes) {
            index.add(inventoryItem, record);
        }
    }
}

