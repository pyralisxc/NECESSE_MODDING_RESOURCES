/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Predicate;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class SettlementStorageGlobalIngredientIDIndex
extends SettlementStorageIndex {
    protected HashMap<Integer, SettlementStorageRecordsRegionData> regions = new HashMap();

    public SettlementStorageGlobalIngredientIDIndex(Level level) {
        super(level);
    }

    @Override
    public void clear() {
        this.regions.clear();
    }

    protected SettlementStorageRecordsRegionData getRegionData(int globalIngredientID) {
        return this.regions.compute(globalIngredientID, (id, last) -> {
            if (last == null) {
                return new SettlementStorageRecordsRegionData(this, item -> item.item.isGlobalIngredient(globalIngredientID));
            }
            return last;
        });
    }

    @Override
    public void add(InventoryItem inventoryItem, SettlementStorageRecord record) {
        for (int globalIngredientID : inventoryItem.item.getGlobalIngredients()) {
            this.getRegionData(globalIngredientID).add(record);
        }
    }

    public SettlementStorageRecordsRegionData getGlobalIngredient(int globalIngredientID) {
        return this.regions.get(globalIngredientID);
    }

    public LinkedList<SettlementStoragePickupSlot> findPickupSlots(int globalIngredientID, EntityJobWorker worker, Predicate<InventoryItem> filter, int minAmount, int maxAmount) {
        SettlementStorageRecordsRegionData data = this.getGlobalIngredient(globalIngredientID);
        if (data != null) {
            return data.startFinder(worker).findPickupSlots(minAmount, maxAmount, filter);
        }
        return null;
    }
}

