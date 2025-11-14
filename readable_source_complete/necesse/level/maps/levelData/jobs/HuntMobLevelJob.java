/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.HuntMobActiveJob;
import necesse.level.maps.levelData.jobs.EntityLevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HuntMobLevelJob
extends EntityLevelJob<Mob> {
    public HuntMobLevelJob(Mob target) {
        super(target);
    }

    public HuntMobLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public static <T extends HuntMobLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        GameMessage targetDescription = ((Mob)((HuntMobLevelJob)foundJob.job).target).getLocalization();
        LocalMessage activityDescription = new LocalMessage("activities", "hunting", "target", targetDescription);
        GameLinkedListJobSequence sequence = new GameLinkedListJobSequence(activityDescription);
        sequence.add(((HuntMobLevelJob)foundJob.job).getActiveJob(worker, foundJob.priority, sequence, "woodsword", new GameDamage(DamageTypeRegistry.MELEE, 20.0f), 64, 500, 500).addRangedAttack("woodbow", new GameDamage(DamageTypeRegistry.RANGED, 20.0f), 256, 500, 1000, "stonearrow", 100));
        return sequence;
    }

    public static JobTypeHandler.JobStreamSupplier<? extends HuntMobLevelJob> getJobStreamer() {
        return (worker, handler) -> {
            Mob mob = worker.getMobWorker();
            ZoneTester restrictZone = worker.getJobRestrictZone();
            return mob.getLevel().entityManager.mobs.streamInRegionsShape(worker.getJobSearchBounds(), 0).filter(m -> !m.removed() && mob.isSamePlace((Entity)m) && m.isCritter).filter(m -> restrictZone.containsTile(m.getTileX(), m.getTileY())).map(m -> ((CritterMob)m).huntJob).filter(Objects::nonNull).filter(EntityLevelJob::isValid);
        };
    }

    public HuntMobActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, GameLinkedListJobSequence sequence, String attackItemStringID, GameDamage damage, int attackRange, int attackAnimTime, int attackCooldown) {
        return new HuntMobActiveJob(worker, priority, this, sequence, attackItemStringID, damage, attackRange, attackAnimTime, attackCooldown);
    }
}

