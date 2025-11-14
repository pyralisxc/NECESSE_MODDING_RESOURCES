/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Predicate;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class SettlementStorageItemIDIndex
extends SettlementStorageIndex {
    protected HashMap<Integer, SettlementStorageRecordsRegionData> regions = new HashMap();

    public SettlementStorageItemIDIndex(Level level) {
        super(level);
    }

    @Override
    public void clear() {
        this.regions.clear();
    }

    protected SettlementStorageRecordsRegionData getRegionData(Item item) {
        return this.regions.compute(item.getID(), (id, last) -> {
            if (last == null) {
                return new SettlementStorageRecordsRegionData(this, other -> item.getID() == other.item.getID());
            }
            return last;
        });
    }

    @Override
    public void add(InventoryItem inventoryItem, SettlementStorageRecord record) {
        this.getRegionData(inventoryItem.item).add(record);
    }

    public int getTotalItems(int itemID) {
        SettlementStorageRecordsRegionData data = this.regions.get(itemID);
        if (data != null) {
            return data.getTotalItems();
        }
        return 0;
    }

    public int getTotalItems(Item item) {
        return this.getTotalItems(item.getID());
    }

    public int getTotalItems(String itemStringID) {
        return this.getTotalItems(ItemRegistry.getItemID(itemStringID));
    }

    public SettlementStorageRecordsRegionData getItem(int itemID) {
        return this.regions.get(itemID);
    }

    public SettlementStorageRecordsRegionData getItem(Item item) {
        return this.getItem(item.getID());
    }

    public SettlementStorageRecordsRegionData getItem(String itemStringID) {
        return this.getItem(ItemRegistry.getItemID(itemStringID));
    }

    public LinkedList<SettlementStoragePickupSlot> findPickupSlots(int itemID, EntityJobWorker worker, Predicate<InventoryItem> filter, int minAmount, int maxAmount) {
        SettlementStorageRecordsRegionData data = this.getItem(itemID);
        if (data != null) {
            return data.startFinder(worker).findPickupSlots(minAmount, maxAmount, filter);
        }
        return null;
    }

    public LinkedList<SettlementStoragePickupSlot> findPickupSlots(Item item, EntityJobWorker worker, Predicate<InventoryItem> filter, int minAmount, int maxAmount) {
        return this.findPickupSlots(item.getID(), worker, filter, minAmount, maxAmount);
    }

    public LinkedList<SettlementStoragePickupSlot> findPickupSlots(String itemStringID, EntityJobWorker worker, Predicate<InventoryItem> filter, int minAmount, int maxAmount) {
        return this.findPickupSlots(ItemRegistry.getItemID(itemStringID), worker, filter, minAmount, maxAmount);
    }
}

