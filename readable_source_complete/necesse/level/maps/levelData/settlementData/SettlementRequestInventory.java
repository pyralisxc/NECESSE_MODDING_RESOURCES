/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.Iterator;
import java.util.Map;
import necesse.engine.util.GameLinkedList;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.settlementData.SettlementOEInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public abstract class SettlementRequestInventory
extends SettlementOEInventory {
    protected SettlementRequestOptions requestOptions;
    protected ItemCategoriesFilter filter;

    public SettlementRequestInventory(Level level, int tileX, int tileY, SettlementRequestOptions requestOptions) {
        super(level, tileX, tileY, true);
        this.requestOptions = requestOptions;
        this.filter = new ItemCategoriesFilter(requestOptions.minAmount, requestOptions.maxAmount);
        this.refreshOEInventory();
    }

    @Override
    public ItemCategoriesFilter getFilter() {
        return this.filter;
    }

    public void addHaulJobs(Level level, SettlementStorageRecords records, int priority) {
        InventoryRange range = this.getInventoryRange();
        if (range != null) {
            int currentItemCount = 0;
            for (int i = range.startSlot; i <= range.endSlot; ++i) {
                if ((currentItemCount += range.inventory.getAmount(i)) < this.requestOptions.minAmount) continue;
                return;
            }
            SettlementStorageRecordsRegionData recordsData = this.requestOptions.getRequestStorageData(records);
            if (recordsData != null) {
                Iterator it = recordsData.streamAllRecords().flatMap(e -> e.entrySet().stream()).iterator();
                block1: while (it.hasNext()) {
                    Map.Entry next = (Map.Entry)it.next();
                    int requestCount = this.requestOptions.maxAmount - currentItemCount;
                    for (SettlementStorageRecord record : (GameLinkedList)next.getValue()) {
                        int addAmount = Math.min(requestCount, record.itemAmount);
                        HaulFromLevelJob job = record.storage.haulFromLevelJobs.stream().filter(e -> e.item.equals(this.level, record.getItem(), true, false, "pickups")).findFirst().orElse(null);
                        if (job != null) {
                            if (job.dropOffPositions.stream().noneMatch(e -> e.storage == this)) {
                                job.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(this, priority, addAmount));
                            }
                        } else {
                            HaulFromLevelJob newJob = new HaulFromLevelJob(record.storage, record.getItem().copy());
                            newJob.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(this, priority, addAmount));
                            record.storage.haulFromLevelJobs.add(newJob);
                            level.jobsLayer.addJob(newJob, true);
                        }
                        if ((requestCount -= addAmount) > 0) continue;
                        continue block1;
                    }
                }
            }
        }
    }
}

