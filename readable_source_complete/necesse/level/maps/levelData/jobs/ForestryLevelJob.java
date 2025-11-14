/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.entity.ObjectDamageResult;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.MineObjectActiveJob;
import necesse.level.gameObject.ForestryJobObject;
import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.MineObjectLevelJob;
import necesse.level.maps.levelData.jobs.PlantSaplingLevelJob;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;

public class ForestryLevelJob
extends MineObjectLevelJob {
    public SettlementForestryZone zone;
    public boolean destroySapling;

    public ForestryLevelJob(int tileX, int tileY, SettlementForestryZone zone, boolean destroySapling) {
        super(tileX, tileY);
        this.zone = zone;
        this.destroySapling = destroySapling;
    }

    public ForestryLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValid() {
        if (this.zone.isRemoved() || !this.destroySapling && !this.zone.isChoppingAllowed()) {
            return false;
        }
        return super.isValid();
    }

    @Override
    public boolean isValidObject(LevelObject object) {
        if (!this.zone.containsTile(this.tileX, this.tileY)) {
            return false;
        }
        if (this.destroySapling && object.object instanceof ForestrySaplingObject && !this.zone.replantChoppedDownTrees()) {
            return true;
        }
        return object.object instanceof ForestryJobObject;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public static <T extends ForestryLevelJob> JobSequence getJobSequence(EntityJobWorker worker, final boolean useItem, final FoundJob<T> foundJob) {
        GameObject object = ((ForestryLevelJob)foundJob.job).getObject().object;
        LocalMessage activityDescription = new LocalMessage("activities", "chopping", "target", object.getLocalization());
        final GameLinkedListJobSequence sequence = new GameLinkedListJobSequence(activityDescription);
        sequence.add(new MineObjectActiveJob(worker, foundJob.priority, ((ForestryLevelJob)foundJob.job).tileX, ((ForestryLevelJob)foundJob.job).tileY, lo -> !((ForestryLevelJob)foundJob.job).isRemoved() && ((ForestryLevelJob)foundJob.job).isValidObject((LevelObject)lo), ((ForestryLevelJob)foundJob.job).reservable, "ironaxe", 20, 500, 0){

            @Override
            public void onObjectDestroyed(ObjectDamageResult result) {
                this.addItemPickupJobs(foundJob.priority, result, sequence);
                PlantSaplingLevelJob newJob = ((ForestryLevelJob)foundJob.job).zone.getNewPlantJob(this.tileX, this.tileY, result.levelObject.object);
                if (newJob != null) {
                    int jobTypeID = LevelJobRegistry.getJobTypeID(PlantSaplingLevelJob.class);
                    newJob.reservable.reserve(this.worker.getMobWorker());
                    sequence.addLast(newJob.getActiveJob(this.worker, this.worker.getJobTypeHandler().getPriority(jobTypeID), useItem));
                }
                ((ForestryLevelJob)foundJob.job).remove();
            }
        });
        return sequence;
    }
}

