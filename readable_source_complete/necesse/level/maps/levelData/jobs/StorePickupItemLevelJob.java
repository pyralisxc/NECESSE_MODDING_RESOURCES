/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.PickupItemEntityActiveJob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.ItemPickupReservedAmount;
import necesse.level.maps.levelData.jobs.EntityLevelJob;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class StorePickupItemLevelJob
extends EntityLevelJob<ItemPickupEntity> {
    public StorePickupItemLevelJob(ItemPickupEntity target) {
        super(target);
    }

    public StorePickupItemLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public static <T extends StorePickupItemLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        return ((StorePickupItemLevelJob)foundJob.job).getJobSequence(worker, foundJob.priority);
    }

    public static JobTypeHandler.JobStreamSupplier<? extends StorePickupItemLevelJob> getJobStreamer() {
        return (worker, handler) -> {
            Mob mob = worker.getMobWorker();
            ZoneTester restrictZone = worker.getJobRestrictZone();
            return mob.getLevel().entityManager.pickups.streamInRegionsShape(worker.getJobSearchBounds(), 0).filter(p -> !p.removed() && mob.isSamePlace((Entity)p) && p.getTimeSinceSpawned() > 10000L && p instanceof ItemPickupEntity).filter(p -> restrictZone.containsTile(p.getTileX(), p.getTileY())).map(p -> (ItemPickupEntity)p).filter(ItemPickupEntity::canBePickedUpBySettlers).map(StorePickupItemLevelJob::new);
        };
    }

    public LinkedListJobSequence getJobSequence(EntityJobWorker worker, JobTypeHandler.TypePriority priority) {
        LocalMessage targetDescription = new LocalMessage("activities", "theground");
        GameMessage itemDescription = ((ItemPickupEntity)this.target).item.getItemLocalization();
        LocalMessage activityDescription = new LocalMessage("activities", "hauling", "item", itemDescription, "target", targetDescription);
        LinkedListJobSequence jobs = new LinkedListJobSequence(activityDescription);
        if (this.addToJobSequence(worker, priority, jobs)) {
            return jobs;
        }
        return null;
    }

    public boolean addToJobSequence(EntityJobWorker worker, JobTypeHandler.TypePriority priority, final LinkedListJobSequence sequence) {
        ItemPickupReservedAmount pickup;
        int availableAmount = ((ItemPickupEntity)this.target).getAvailableAmount();
        if (availableAmount <= 0) {
            return false;
        }
        WorkInventory workInventory = worker.getWorkInventory();
        availableAmount = Math.min(workInventory.getCanAddAmount(((ItemPickupEntity)this.target).item), availableAmount);
        if (availableAmount <= 0) {
            return false;
        }
        ArrayList<HasStorageLevelJob.DropOffFind> dropOffLocations = HasStorageLevelJob.findDropOffLocation(worker, ((ItemPickupEntity)this.target).item.copy(Math.min(availableAmount, ((ItemPickupEntity)this.target).item.itemStackSize())), ((ItemPickupEntity)this.target).getPositionPoint());
        int amount = 0;
        LinkedList<DropOffSettlementStorageActiveJob> dropOffJobs = new LinkedList<DropOffSettlementStorageActiveJob>();
        for (HasStorageLevelJob.DropOffFind dropOffLocation : dropOffLocations) {
            amount += dropOffLocation.item.getAmount();
            dropOffJobs.add(dropOffLocation.getActiveJob(worker, priority, false));
        }
        if (amount > 0 && (pickup = ((ItemPickupEntity)this.target).reservePickupAmount(amount)) != null) {
            for (DropOffSettlementStorageActiveJob dropOffJob : dropOffJobs) {
                sequence.addLast(dropOffJob);
            }
            sequence.addFirst(new PickupItemEntityActiveJob(worker, priority, pickup){

                @Override
                public ActiveJobResult performTarget() {
                    ActiveJobResult result = super.performTarget();
                    if (result == ActiveJobResult.FINISHED && !this.worker.getWorkInventory().isFull()) {
                        JobTypeHandler.SubHandler<StorePickupItemLevelJob> jobHandler = this.worker.getJobTypeHandler().getJobHandler(StorePickupItemLevelJob.class);
                        Comparator<FoundJob> comparator = Comparator.comparingInt(o -> -((StorePickupItemLevelJob)o.job).getSameJobPriority());
                        comparator = comparator.thenComparingDouble(FoundJob::getDistanceFromWorker);
                        Optional<FoundJob> optional = jobHandler.streamFoundJobs(this.worker).sorted(comparator).filter(FoundJob::canMoveTo).filter(e -> ((StorePickupItemLevelJob)e.job).addToJobSequence(this.worker, e.priority, sequence)).findFirst();
                    }
                    return result;
                }
            });
            return true;
        }
        return false;
    }
}

