/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class PickupSettlementStorageActiveJob
extends TileActiveJob {
    public SettlementStoragePickupSlot slot;
    public AtomicReference<InventoryItem> pickedUpItemRef;

    public PickupSettlementStorageActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, int tileX, int tileY, SettlementStoragePickupSlot slot, AtomicReference<InventoryItem> pickedUpItemRef) {
        super(worker, priority, tileX, tileY);
        this.slot = slot;
        this.pickedUpItemRef = pickedUpItemRef;
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        return new JobMoveToTile(this.tileX, this.tileY, true);
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        this.slot.reserve(this.worker.getMobWorker());
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        return this.slot.isValid(this.getTileInventory());
    }

    @Override
    public void onCancelled(boolean becauseOfInvalid, boolean isCurrent, boolean isMovingTo) {
        super.onCancelled(becauseOfInvalid, isCurrent, isMovingTo);
        if (!this.slot.isRemoved()) {
            this.slot.remove();
        }
    }

    @Override
    public ActiveJobResult perform() {
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        InventoryItem item = this.slot.pickupItem(this.getTileInventory());
        if (item != null) {
            this.pickedUpItemRef.set(item.copy());
            this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, item.item, 250);
            this.worker.getWorkInventory().add(item);
            return ActiveJobResult.FINISHED;
        }
        return ActiveJobResult.FAILED;
    }

    public static SettlementStorageRecords getStorageRecords(EntityJobWorker worker) {
        Mob mob = worker.getMobWorker();
        if (mob instanceof SettlerMob) {
            ServerSettlementData settlement = ((SettlerMob)((Object)mob)).getSettlerSettlementServerData();
            return settlement != null ? settlement.storageRecords : null;
        }
        return null;
    }

    @Deprecated
    public static ArrayList<PickupSettlementStorageActiveJob> findItems(EntityJobWorker worker, JobTypeHandler.TypePriority priority, ItemFilter filter, int minAmount, int maxAmount) {
        return PickupSettlementStorageActiveJob.findItems(worker, priority, filter::matchesItem, minAmount, maxAmount);
    }

    @Deprecated
    public static ArrayList<PickupSettlementStorageActiveJob> findItems(EntityJobWorker worker, JobTypeHandler.TypePriority priority, Predicate<InventoryItem> filter, int minAmount, int maxAmount) {
        Mob mob = worker.getMobWorker();
        return Performance.record((PerformanceTimerManager)mob.getLevel().tickManager(), "findItems", () -> {
            ZoneTester restrictZone = worker.getJobRestrictZone();
            Point base = worker.getJobSearchTile();
            GameLinkedList storageJobs = worker.streamJobsWithinRange().filter(e -> e.getID() == LevelJobRegistry.hasStorageID).filter(e -> restrictZone.containsTile(e.tileX, e.tileY)).map(e -> (HasStorageLevelJob)e).map(e -> new ComputedObjectValue<HasStorageLevelJob, Integer>((HasStorageLevelJob)e, () -> {
                int amount = 0;
                InventoryRange range = e.settlementInventory.getInventoryRange();
                if (range != null) {
                    InventoryItem item;
                    for (int slot = range.startSlot; !(slot > range.endSlot || (item = range.inventory.getItem(slot)) != null && filter.test(item) && (amount += item.getAmount()) >= maxAmount); ++slot) {
                    }
                }
                return amount;
            })).filter(e -> e.get() != null).filter(e -> worker.estimateCanMoveTo(((HasStorageLevelJob)e.object).tileX, ((HasStorageLevelJob)e.object).tileY, true)).collect(GameLinkedList::new, GameLinkedList::add, GameLinkedList::addAll);
            ArrayList<PickupSettlementStorageActiveJob> jobs = new ArrayList<PickupSettlementStorageActiveJob>();
            HasStorageLevelJob lastPickupLocation = null;
            int amount = 0;
            while (amount < maxAmount && !storageJobs.isEmpty()) {
                Point from = lastPickupLocation == null ? new Point(mob.getX(), mob.getY()) : new Point(lastPickupLocation.tileX * 32 + 16, lastPickupLocation.tileY * 32 + 16);
                GameLinkedList.Element best = storageJobs.streamElements().min(Comparator.comparingDouble(e -> from.distance(((HasStorageLevelJob)((ComputedObjectValue)e.object).object).tileX * 32 + 16, ((HasStorageLevelJob)((ComputedObjectValue)e.object).object).tileY * 32 + 16))).orElse(null);
                if (best == null) break;
                LinkedList<SettlementStoragePickupSlot> slots = ((HasStorageLevelJob)((ComputedObjectValue)best.object).object).settlementInventory.findUnreservedSlots(filter, 1, maxAmount - amount);
                if (slots != null) {
                    for (SettlementStoragePickupSlot slot : slots) {
                        slot.reserve(worker.getMobWorker());
                        amount += slot.item.getAmount();
                        jobs.add(new PickupSettlementStorageActiveJob(worker, priority, ((HasStorageLevelJob)((ComputedObjectValue)best.object).object).tileX, ((HasStorageLevelJob)((ComputedObjectValue)best.object).object).tileY, slot, new AtomicReference<InventoryItem>()));
                    }
                }
                if (amount >= maxAmount) break;
                lastPickupLocation = (HasStorageLevelJob)((ComputedObjectValue)best.object).object;
                best.remove();
            }
            if (amount >= minAmount) {
                return jobs;
            }
            return null;
        });
    }
}

