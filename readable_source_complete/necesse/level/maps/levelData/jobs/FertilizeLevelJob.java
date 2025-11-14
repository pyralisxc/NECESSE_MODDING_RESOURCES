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
import necesse.engine.util.GameObjectReservable;
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
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SeedObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.MineObjectLevelJob;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.zones.SettlementFertilizeZone;

public class FertilizeLevelJob
extends MineObjectLevelJob {
    public SettlementFertilizeZone zone;

    public FertilizeLevelJob(int tileX, int tileY, SettlementFertilizeZone zone, GameObjectReservable reservable) {
        super(tileX, tileY);
        this.zone = zone;
        if (reservable != null) {
            this.reservable = reservable;
        }
    }

    public FertilizeLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValidObject(LevelObject object) {
        if (this.zone.isRemoved() || !this.zone.containsTile(this.tileX, this.tileY)) {
            return false;
        }
        ObjectEntity objEnt = object.getObjectEntity();
        if (objEnt instanceof SeedObjectEntity) {
            return !((SeedObjectEntity)objEnt).isFertilized();
        }
        return false;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public static Object getPreSequenceCompute(EntityJobWorker worker, JobTypeHandler.SubHandler<FertilizeLevelJob> handler) {
        return new FoundFertilizer(worker, handler);
    }

    public static <T extends FertilizeLevelJob> JobSequence getJobSequence(EntityJobWorker worker, boolean useItem, FoundJob<T> foundJob) {
        GameObject object = ((FertilizeLevelJob)foundJob.job).getLevel().getObject(((FertilizeLevelJob)foundJob.job).tileX, ((FertilizeLevelJob)foundJob.job).tileY);
        LocalMessage activityDescription = new LocalMessage("activities", "fertilizing", "target", object.getLocalization());
        LinkedListJobSequence sequence = new LinkedListJobSequence(activityDescription);
        if (useItem) {
            FoundFertilizer foundFertilizer = (FoundFertilizer)foundJob.preSequenceCompute.get();
            if (foundFertilizer.hasNoneInWorkInventory) {
                if (foundFertilizer.items != null) {
                    sequence.addAll(foundFertilizer.items);
                } else {
                    return null;
                }
            }
        }
        sequence.add(((FertilizeLevelJob)foundJob.job).getActiveJob(worker, foundJob.priority, useItem));
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
                FertilizeLevelJob.this.reservable.reserve(this.worker.getMobWorker());
            }

            @Override
            public boolean isValid(boolean isCurrent) {
                if (FertilizeLevelJob.this.isRemoved() || !FertilizeLevelJob.this.reservable.isAvailable(this.worker.getMobWorker())) {
                    return false;
                }
                if (isCurrent && useItem) {
                    ItemFilter filter = new ItemFilter(ItemRegistry.getItemID("fertilizer"));
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
                ObjectEntity objEnt = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
                if (objEnt instanceof SeedObjectEntity) {
                    SeedObjectEntity seedEnt = (SeedObjectEntity)objEnt;
                    Item fertilizer = ItemRegistry.getItem("fertilizer");
                    if (useItem) {
                        boolean usedItem = false;
                        WorkInventory workInventory = this.worker.getWorkInventory();
                        ListIterator<InventoryItem> li = workInventory.listIterator();
                        while (li.hasNext()) {
                            InventoryItem next = li.next();
                            if (next.getAmount() <= 0 || next.item.getID() != fertilizer.getID()) continue;
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
                    this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, fertilizer, 250);
                    seedEnt.fertilize();
                    FertilizeLevelJob.this.remove();
                    return ActiveJobResult.FINISHED;
                }
                return ActiveJobResult.FAILED;
            }
        };
    }

    private static class FoundFertilizer {
        public boolean hasNoneInWorkInventory;
        public ArrayList<PickupSettlementStorageActiveJob> items;

        public FoundFertilizer(EntityJobWorker worker, JobTypeHandler.SubHandler<FertilizeLevelJob> handler) {
            LinkedList<SettlementStoragePickupSlot> found;
            SettlementStorageRecords storage;
            TickManager tickManager = worker.getMobWorker().getLevel().tickManager();
            int fertilizerID = ItemRegistry.getItemID("fertilizer");
            this.hasNoneInWorkInventory = Performance.record((PerformanceTimerManager)tickManager, "lookInventory", () -> worker.getWorkInventory().stream().noneMatch(i -> i.item.getID() == fertilizerID));
            if (this.hasNoneInWorkInventory && (storage = PickupSettlementStorageActiveJob.getStorageRecords(worker)) != null && (found = storage.getIndex(SettlementStorageItemIDIndex.class).findPickupSlots(fertilizerID, worker, null, 1, 10)) != null) {
                this.items = found.stream().map(slot -> slot.toPickupJob(worker, handler.priority)).collect(Collectors.toCollection(ArrayList::new));
            }
        }
    }
}

