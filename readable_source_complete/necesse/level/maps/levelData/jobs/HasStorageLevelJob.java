/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.jobs.UseWorkstationLevelJob;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HasStorageLevelJob
extends LevelJob {
    public SettlementInventory settlementInventory;
    private Supplier<Boolean> validCheck;

    public HasStorageLevelJob(SettlementInventory settlementInventory, Supplier<Boolean> validCheck) {
        super(settlementInventory.tileX, settlementInventory.tileY);
        this.settlementInventory = settlementInventory;
        this.validCheck = validCheck;
        this.reservable = new GameObjectReservable(){

            @Override
            public boolean isAvailable(Object worker, WorldEntity worldEntity) {
                return true;
            }
        };
    }

    public HasStorageLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isValid() {
        return this.getInventory() != null && this.validCheck.get() != false;
    }

    public Inventory getInventory() {
        ObjectEntity objectEntity = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        if (objectEntity instanceof OEInventory) {
            return ((OEInventory)((Object)objectEntity)).getInventory();
        }
        return null;
    }

    @Override
    public int getSameJobPriority() {
        return this.settlementInventory.priority;
    }

    @Override
    public boolean prioritizeForSameJobAgain() {
        return true;
    }

    public static <T extends HasStorageLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        if (worker.getWorkInventory().isEmpty()) {
            return null;
        }
        for (InventoryItem item : worker.getWorkInventory().items()) {
            int addAmount = ((HasStorageLevelJob)foundJob.job).settlementInventory.canAddFutureDropOff(item);
            if (addAmount <= 0) continue;
            InventoryItem addItem = item.copy();
            LocalMessage activityDescription = new LocalMessage("activities", "droppingoffinv");
            return new SingleJobSequence(new DropOffSettlementStorageActiveJob(worker, foundJob.priority, ((HasStorageLevelJob)foundJob.job).settlementInventory, null, null, false, () -> addItem), activityDescription);
        }
        return null;
    }

    public static Stream<HasStorageLevelJob> streamStorageJobs(EntityJobWorker worker, JobTypeHandler.SubHandler<?> handler) {
        ZoneTester restrictZone = worker.getJobRestrictZone();
        int jobID = LevelJobRegistry.hasStorageID;
        return worker.streamJobsWithinRange().filter(j -> j.getID() == jobID).filter(e -> restrictZone.containsTile(e.tileX, e.tileY)).map(e -> (HasStorageLevelJob)e);
    }

    public static Stream<SettlementStoragePickupFuture> findPickupItems(EntityJobWorker worker, JobTypeHandler.SubHandler<?> handler, Predicate<InventoryItem> filter) {
        return HasStorageLevelJob.streamStorageJobs(worker, handler).filter(e -> worker.estimateCanMoveTo(e.settlementInventory.tileX, e.settlementInventory.tileY, true)).flatMap(j -> j.settlementInventory.findFutureUnreservedSlots().filter(p -> filter.test(p.item)));
    }

    public static int getItemCount(EntityJobWorker worker, Predicate<InventoryItem> filter, int maxCount, boolean includeDropOffSimulation, boolean includeFutureWorkstationOutput) {
        ZoneTester restrictZone = worker.getJobRestrictZone();
        AtomicInteger count = new AtomicInteger();
        int jobID = LevelJobRegistry.hasStorageID;
        count.addAndGet(((Stream)worker.streamJobsWithinRange().filter(j -> j.getID() == jobID).filter(e -> restrictZone.containsTile(e.tileX, e.tileY)).map(e -> (HasStorageLevelJob)e).sequential()).reduce(0, (last, storageJob) -> {
            int nextMaxCount = maxCount - last - count.get();
            if (nextMaxCount > 0) {
                last = last + storageJob.settlementInventory.getItemCount(filter, nextMaxCount, includeDropOffSimulation);
            }
            return last;
        }, Integer::sum));
        if (count.get() < maxCount && includeFutureWorkstationOutput) {
            jobID = LevelJobRegistry.useWorkstationID;
            count.addAndGet(((Stream)worker.streamJobsWithinRange().filter(j -> j.getID() == jobID).filter(e -> restrictZone.containsTile(e.tileX, e.tileY)).map(e -> (UseWorkstationLevelJob)e).sequential()).reduce(0, (last, workstationJob) -> {
                SettlementWorkstationLevelObject workstationObject;
                int nextMaxCount = maxCount - last - count.get();
                if (nextMaxCount > 0 && (workstationObject = workstationJob.workstation.getWorkstationObject()) != null) {
                    int nextCount = 0;
                    for (InventoryItem item : workstationObject.getCurrentAndFutureProcessingOutputs()) {
                        if (item == null || !filter.test(item) || (nextCount += item.getAmount()) < nextMaxCount) continue;
                        return nextMaxCount;
                    }
                    last = last + nextCount;
                }
                return last;
            }, Integer::sum));
        }
        return Math.min(count.get(), maxCount);
    }

    public static ArrayList<DropOffFind> findDropOffLocation(EntityJobWorker worker, InventoryItem item) {
        return HasStorageLevelJob.findDropOffLocation(worker, item, null);
    }

    public static ArrayList<DropOffFind> findDropOffLocation(EntityJobWorker worker, InventoryItem item, Point startPosition) {
        Mob mob = worker.getMobWorker();
        int jobID = LevelJobRegistry.hasStorageID;
        ZoneTester restrictZone = worker.getJobRestrictZone();
        GameLinkedList storageJobs = worker.streamJobsWithinRange().filter(j -> j.getID() == jobID).filter(e -> restrictZone.containsTile(e.tileX, e.tileY)).map(e -> (HasStorageLevelJob)e).map(storageJob -> new ComputedObjectValue<HasStorageLevelJob, Integer>((HasStorageLevelJob)storageJob, () -> {
            int addAmount = storageJob.settlementInventory.canAddFutureDropOff(item);
            return Math.max(addAmount, 0);
        })).filter(e -> (Integer)e.get() > 0).filter(e -> worker.estimateCanMoveTo(((HasStorageLevelJob)e.object).tileX, ((HasStorageLevelJob)e.object).tileY, true)).collect(GameLinkedList::new, GameLinkedList::add, GameLinkedList::addAll);
        ArrayList<DropOffFind> jobs = new ArrayList<DropOffFind>();
        HasStorageLevelJob lastPickupLocation = null;
        int amount = 0;
        while (amount < item.getAmount() && !storageJobs.isEmpty()) {
            Point from = lastPickupLocation != null ? new Point(lastPickupLocation.tileX * 32 + 16, lastPickupLocation.tileY * 32 + 16) : (startPosition != null ? startPosition : new Point(mob.getX(), mob.getY()));
            Comparator<GameLinkedList.Element> comparator = (o1, o2) -> -Integer.compare(((HasStorageLevelJob)((ComputedObjectValue)o1.object).object).getSameJobPriority(), ((HasStorageLevelJob)((ComputedObjectValue)o2.object).object).getSameJobPriority());
            comparator = comparator.thenComparingDouble(e -> from.distance(((HasStorageLevelJob)((ComputedObjectValue)e.object).object).tileX * 32 + 16, ((HasStorageLevelJob)((ComputedObjectValue)e.object).object).tileY * 32 + 16));
            GameLinkedList.Element best = storageJobs.streamElements().min(comparator).orElse(null);
            if (best == null) break;
            int remaining = item.getAmount() - amount;
            int dropOffAmount = Math.min((Integer)((ComputedObjectValue)best.object).get(), remaining);
            InventoryItem dropOffItem = item.copy(dropOffAmount);
            jobs.add(new DropOffFind(((HasStorageLevelJob)((ComputedObjectValue)best.object).object).settlementInventory, dropOffItem));
            if ((amount += dropOffAmount) >= item.getAmount()) break;
            lastPickupLocation = (HasStorageLevelJob)((ComputedObjectValue)best.object).object;
            best.remove();
        }
        return jobs;
    }

    public static class DropOffFind {
        public final SettlementInventory inventory;
        public final InventoryItem item;

        public DropOffFind(SettlementInventory inventory, InventoryItem item) {
            this.inventory = inventory;
            this.item = item;
        }

        public DropOffSettlementStorageActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, GameObjectReservable reservable, Supplier<Boolean> isRemovedCheck, boolean requireFullAmount) {
            return new DropOffSettlementStorageActiveJob(worker, priority, this.inventory, reservable, isRemovedCheck, requireFullAmount, () -> this.item);
        }

        public DropOffSettlementStorageActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, boolean requireFullAmount) {
            return new DropOffSettlementStorageActiveJob(worker, priority, this.inventory, null, null, requireFullAmount, () -> this.item);
        }
    }
}

