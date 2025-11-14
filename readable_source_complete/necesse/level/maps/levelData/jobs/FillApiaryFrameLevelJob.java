/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.MineObjectLevelJob;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class FillApiaryFrameLevelJob
extends MineObjectLevelJob {
    public FillApiaryFrameLevelJob(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public FillApiaryFrameLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValidObject(LevelObject object) {
        AbstractBeeHiveObjectEntity apiaryEntity = object.getCurrentObjectEntity(AbstractBeeHiveObjectEntity.class);
        if (apiaryEntity != null) {
            return apiaryEntity.canAddFrame();
        }
        return false;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public static Object getPreSequenceCompute(EntityJobWorker worker, JobTypeHandler.SubHandler<FillApiaryFrameLevelJob> handler) {
        return new FoundApiaryFrame(worker, handler);
    }

    public static <T extends FillApiaryFrameLevelJob> JobSequence getJobSequence(EntityJobWorker worker, boolean useItem, FoundJob<T> foundJob) {
        LocalMessage activityDescription = new LocalMessage("activities", "fillingapiary");
        LinkedListJobSequence sequence = new LinkedListJobSequence(activityDescription);
        if (useItem) {
            FoundApiaryFrame foundApiaryFrame = (FoundApiaryFrame)foundJob.preSequenceCompute.get();
            if (foundApiaryFrame.hasNoneInWorkInventory) {
                if (foundApiaryFrame.items != null) {
                    sequence.addAll(foundApiaryFrame.items);
                } else {
                    return null;
                }
            }
        }
        sequence.add(((FillApiaryFrameLevelJob)foundJob.job).getActiveJob(worker, foundJob.priority, useItem));
        return sequence;
    }

    public ActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, final boolean useItem) {
        return new TileActiveJob(worker, priority, this.tileX, this.tileY){

            @Override
            public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
                return new JobMoveToTile(this.tileX, this.tileY, true);
            }

            @Override
            public void tick(boolean isCurrent, boolean isMovingTo) {
                FillApiaryFrameLevelJob.this.reservable.reserve(this.worker.getMobWorker());
            }

            @Override
            public boolean isValid(boolean isCurrent) {
                if (FillApiaryFrameLevelJob.this.isRemoved() || !FillApiaryFrameLevelJob.this.reservable.isAvailable(this.worker.getMobWorker())) {
                    return false;
                }
                if (isCurrent && useItem) {
                    ItemFilter filter = new ItemFilter(ItemRegistry.getItemID("apiaryframe"));
                    for (InventoryItem item : this.worker.getWorkInventory().items()) {
                        if (!filter.matchesItem(item)) continue;
                        return true;
                    }
                    return false;
                }
                return true;
            }

            @Override
            public ActiveJobResult perform() {
                if (this.worker.isInWorkAnimation()) {
                    return ActiveJobResult.PERFORMING;
                }
                AbstractBeeHiveObjectEntity apiaryEntity = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY, AbstractBeeHiveObjectEntity.class);
                if (apiaryEntity != null) {
                    if (!apiaryEntity.canAddFrame()) {
                        return ActiveJobResult.FAILED;
                    }
                    Item apiaryFrame = ItemRegistry.getItem("apiaryframe");
                    if (useItem) {
                        boolean usedItem = false;
                        WorkInventory workInventory = this.worker.getWorkInventory();
                        ListIterator<InventoryItem> li = workInventory.listIterator();
                        while (li.hasNext()) {
                            InventoryItem next = li.next();
                            if (next.getAmount() <= 0 || next.item.getID() != apiaryFrame.getID()) continue;
                            next.setAmount(next.getAmount() - 1);
                            if (next.getAmount() <= 0) {
                                li.remove();
                            }
                            workInventory.markDirty();
                            usedItem = true;
                        }
                        if (!usedItem) {
                            return ActiveJobResult.FAILED;
                        }
                    }
                    this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, apiaryFrame, 250);
                    apiaryEntity.addFrame();
                    if (!apiaryEntity.canAddFrame()) {
                        FillApiaryFrameLevelJob.this.remove();
                    }
                    return ActiveJobResult.FINISHED;
                }
                return ActiveJobResult.FAILED;
            }
        };
    }

    private static class FoundApiaryFrame {
        public boolean hasNoneInWorkInventory;
        public ArrayList<PickupSettlementStorageActiveJob> items;

        public FoundApiaryFrame(EntityJobWorker worker, JobTypeHandler.SubHandler<FillApiaryFrameLevelJob> handler) {
            LinkedList<SettlementStoragePickupSlot> found;
            SettlementStorageRecords storage;
            TickManager tickManager = worker.getMobWorker().getLevel().tickManager();
            int apiaryFrameID = ItemRegistry.getItemID("apiaryframe");
            this.hasNoneInWorkInventory = Performance.record((PerformanceTimerManager)tickManager, "lookInventory", () -> worker.getWorkInventory().stream().noneMatch(i -> i.item.getID() == apiaryFrameID));
            if (this.hasNoneInWorkInventory && (storage = PickupSettlementStorageActiveJob.getStorageRecords(worker)) != null && (found = storage.getIndex(SettlementStorageItemIDIndex.class).findPickupSlots(apiaryFrameID, worker, null, 1, 10)) != null) {
                this.items = found.stream().map(slot -> slot.toPickupJob(worker, handler.priority)).collect(Collectors.toCollection(ArrayList::new));
            }
        }
    }
}

