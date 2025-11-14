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
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.MineObjectLevelJob;
import necesse.level.maps.levelData.jobs.PlantCropLevelJob;

public class HarvestCropLevelJob
extends MineObjectLevelJob {
    public HarvestCropLevelJob(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public HarvestCropLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValidObject(LevelObject object) {
        return object.object.isSeed && ((SeedObject)object.object).isLastStage();
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public static <T extends HarvestCropLevelJob> JobSequence getJobSequence(EntityJobWorker worker, final boolean useItem, final FoundJob<T> foundJob) {
        GameObject object = ((HarvestCropLevelJob)foundJob.job).getObject().object;
        LocalMessage activityDescription = new LocalMessage("activities", "harvesting", "target", object.getLocalization());
        final GameLinkedListJobSequence sequence = new GameLinkedListJobSequence(activityDescription);
        sequence.add(new MineObjectActiveJob(worker, foundJob.priority, ((HarvestCropLevelJob)foundJob.job).tileX, ((HarvestCropLevelJob)foundJob.job).tileY, ((HarvestCropLevelJob)foundJob.job)::isValidObject, ((HarvestCropLevelJob)foundJob.job).reservable, "sickle", 100, 500, 0){

            @Override
            public void onObjectDestroyed(ObjectDamageResult result) {
                int firstStageID;
                this.addItemPickupJobs(foundJob.priority, result, sequence);
                if (result.levelObject.object.isSeed && (firstStageID = ((SeedObject)result.levelObject.object).stageIDs[0]) != -1) {
                    PlantCropLevelJob plantJob = new PlantCropLevelJob(this.tileX, this.tileY, firstStageID);
                    plantJob.reservable.reserve(this.worker.getMobWorker());
                    if (this.getLevel().jobsLayer.addJob(plantJob) == plantJob) {
                        int jobTypeID = LevelJobRegistry.getJobTypeID(PlantCropLevelJob.class);
                        sequence.addLast(plantJob.getActiveJob(this.worker, this.worker.getJobTypeHandler().getPriority(jobTypeID), useItem));
                    }
                }
                ((HarvestCropLevelJob)foundJob.job).remove();
            }
        });
        return sequence;
    }
}

