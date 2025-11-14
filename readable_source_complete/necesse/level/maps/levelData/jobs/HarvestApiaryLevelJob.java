/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.entity.objectEntity.ApiaryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.LevelJob;

public class HarvestApiaryLevelJob
extends LevelJob {
    public HarvestApiaryLevelJob(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public HarvestApiaryLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValid() {
        ApiaryObjectEntity ent = this.getObjectEntity();
        return ent != null && ent.getHoneyAmount() > 0;
    }

    public ApiaryObjectEntity getObjectEntity() {
        ObjectEntity objectEntity = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        if (objectEntity instanceof ApiaryObjectEntity) {
            return (ApiaryObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public ArrayList<InventoryItem> harvest() {
        ApiaryObjectEntity ent = this.getObjectEntity();
        if (ent != null) {
            ArrayList<InventoryItem> harvestItems = ent.getHarvestItems();
            ent.resetHarvestItems();
            return harvestItems;
        }
        this.remove();
        return new ArrayList<InventoryItem>();
    }

    public static <T extends HarvestApiaryLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        GameObject object = ((HarvestApiaryLevelJob)foundJob.job).getLevel().getObject(((HarvestApiaryLevelJob)foundJob.job).tileX, ((HarvestApiaryLevelJob)foundJob.job).tileY);
        LocalMessage activityDescription = new LocalMessage("activities", "harvesting", "target", object.getLocalization());
        return new SingleJobSequence(((HarvestApiaryLevelJob)foundJob.job).getActiveJob(worker, foundJob.priority), activityDescription);
    }

    public ActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority) {
        return new TileActiveJob(worker, priority, this.tileX, this.tileY){

            @Override
            public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
                return new JobMoveToTile(this.tileX, this.tileY, true);
            }

            @Override
            public void tick(boolean isCurrent, boolean isMovingTo) {
                HarvestApiaryLevelJob.this.reservable.reserve(this.worker.getMobWorker());
            }

            @Override
            public boolean isValid(boolean isCurrent) {
                if (HarvestApiaryLevelJob.this.isRemoved() || !HarvestApiaryLevelJob.this.reservable.isAvailable(this.worker.getMobWorker())) {
                    return false;
                }
                return HarvestApiaryLevelJob.this.isValid();
            }

            @Override
            public ActiveJobResult perform() {
                if (this.worker.isInWorkAnimation()) {
                    return ActiveJobResult.PERFORMING;
                }
                ArrayList<InventoryItem> items = HarvestApiaryLevelJob.this.harvest();
                if (items.isEmpty()) {
                    this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, null, 250);
                } else {
                    this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, items.get((int)0).item, 250);
                }
                for (InventoryItem item : items) {
                    this.worker.getWorkInventory().add(item);
                }
                return ActiveJobResult.FINISHED;
            }
        };
    }
}

