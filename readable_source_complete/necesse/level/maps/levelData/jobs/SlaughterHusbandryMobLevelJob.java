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
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.HuntMobActiveJob;
import necesse.level.maps.levelData.jobs.EntityLevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;

public class SlaughterHusbandryMobLevelJob
extends EntityLevelJob<Mob> {
    public SettlementHusbandryZone zone;

    public SlaughterHusbandryMobLevelJob(Mob target, SettlementHusbandryZone zone) {
        super(target);
        this.zone = zone;
    }

    public SlaughterHusbandryMobLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        if (this.zone == null) {
            return false;
        }
        if (this.zone.isRemoved()) {
            return false;
        }
        return this.zone.containsTile(((Mob)this.target).getTileX(), ((Mob)this.target).getTileY());
    }

    public static <T extends SlaughterHusbandryMobLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        GameMessage targetDescription = ((Mob)((SlaughterHusbandryMobLevelJob)foundJob.job).target).getLocalization();
        LocalMessage activityDescription = new LocalMessage("activities", "slaughtering", "target", targetDescription);
        GameLinkedListJobSequence sequence = new GameLinkedListJobSequence(activityDescription);
        sequence.add(((SlaughterHusbandryMobLevelJob)foundJob.job).getActiveJob(worker, foundJob.priority, sequence, "woodsword", new GameDamage(DamageTypeRegistry.MELEE, 20.0f), 50, 500, 500));
        return sequence;
    }

    public static JobTypeHandler.JobStreamSupplier<? extends SlaughterHusbandryMobLevelJob> getJobStreamer() {
        return (worker, handler) -> {
            Mob mob = worker.getMobWorker();
            ZoneTester restrictZone = worker.getJobRestrictZone();
            return mob.getLevel().entityManager.mobs.streamInRegionsShape(worker.getJobSearchBounds(), 0).filter(m -> !m.removed() && mob.isSamePlace((Entity)m) && m instanceof HusbandryMob).filter(m -> restrictZone.containsTile(m.getTileX(), m.getTileY())).map(m -> ((HusbandryMob)m).slaughterJob).filter(Objects::nonNull).filter(SlaughterHusbandryMobLevelJob::isValid);
        };
    }

    public HuntMobActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, GameLinkedListJobSequence sequence, String attackItemStringID, GameDamage damage, int attackRange, int attackAnimTime, int attackCooldown) {
        return new HuntMobActiveJob(worker, priority, this, sequence, attackItemStringID, damage, attackRange, attackAnimTime, attackCooldown);
    }
}

