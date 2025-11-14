/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketPlaceObject;
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
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.PlaceObjectLevelJob;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class PlantCropLevelJob
extends PlaceObjectLevelJob {
    public PlantCropLevelJob(int tileX, int tileY, int objectID) {
        super(tileX, tileY, objectID);
    }

    public PlantCropLevelJob(LoadData save) {
        super(save);
    }

    public int getSeedItemID() {
        return this.getObject().getObjectItem().getID();
    }

    public Item plant(WorkInventory inventory) {
        Item plant = null;
        if (inventory != null) {
            ObjectItem objectItem = this.getObject().getObjectItem();
            ListIterator<InventoryItem> li = inventory.listIterator();
            while (li.hasNext()) {
                InventoryItem next = li.next();
                if (next.getAmount() <= 0 || next.item.getID() != objectItem.getID()) continue;
                plant = next.item;
                next.setAmount(next.getAmount() - 1);
                if (next.getAmount() <= 0) {
                    li.remove();
                }
                inventory.markDirty();
                break;
            }
        } else {
            plant = this.getObject().getObjectItem();
        }
        if (plant != null) {
            GameObject object = this.getObject();
            Level level = this.getLevel();
            object.placeObject(level, this.tileX, this.tileY, this.objectRotation, true);
            level.objectLayer.setIsPlayerPlaced(this.tileX, this.tileY, true);
            if (this.isServer()) {
                level.getServer().network.sendToClientsWithTile(new PacketPlaceObject(level, null, 0, this.tileX, this.tileY, this.objectID, this.objectRotation, true), level, this.tileX, this.tileY);
            }
            level.getTile(this.tileX, this.tileY).checkAround(level, this.tileX, this.tileY);
            level.getObject(this.tileX, this.tileY).checkAround(level, this.tileX, this.tileY);
        }
        return plant;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static <T extends PlantCropLevelJob> JobSequence getJobSequence(EntityJobWorker worker, boolean useItem, FoundJob<T> foundJob) {
        GameObject object = ((PlantCropLevelJob)foundJob.job).getObject();
        LocalMessage activityDescription = new LocalMessage("activities", "planting", "item", object.getLocalization());
        LinkedListJobSequence sequence = new LinkedListJobSequence(activityDescription);
        if (useItem) {
            int seedItemID = ((PlantCropLevelJob)foundJob.job).getSeedItemID();
            if (worker.getWorkInventory().stream().noneMatch(item -> item.item.getID() == seedItemID)) {
                SettlementStorageRecords records = PickupSettlementStorageActiveJob.getStorageRecords(worker);
                if (records == null) return null;
                LinkedList<SettlementStoragePickupSlot> pickupSlots = records.getIndex(SettlementStorageItemIDIndex.class).findPickupSlots(seedItemID, worker, null, 1, 10);
                if (pickupSlots == null) return null;
                for (SettlementStoragePickupSlot slot : pickupSlots) {
                    sequence.add(slot.toPickupJob(worker, foundJob.priority));
                }
            }
        }
        sequence.add(((PlantCropLevelJob)foundJob.job).getActiveJob(worker, foundJob.priority, useItem));
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
                PlantCropLevelJob.this.reservable.reserve(this.worker.getMobWorker());
            }

            @Override
            public boolean isValid(boolean isCurrent) {
                if (PlantCropLevelJob.this.isRemoved() || !PlantCropLevelJob.this.reservable.isAvailable(this.worker.getMobWorker())) {
                    return false;
                }
                if (isCurrent && useItem) {
                    int seedItemID = PlantCropLevelJob.this.getSeedItemID();
                    for (InventoryItem item : this.worker.getWorkInventory().items()) {
                        if (item.item.getID() != seedItemID) continue;
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
                Item plant = PlantCropLevelJob.this.plant(useItem ? this.worker.getWorkInventory() : null);
                if (plant != null) {
                    this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, plant, 250);
                    PlantCropLevelJob.this.remove();
                    return ActiveJobResult.FINISHED;
                }
                return ActiveJobResult.FAILED;
            }
        };
    }
}

