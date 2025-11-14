/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.InteractMobActiveJob;
import necesse.entity.mobs.job.activeJob.PickupItemEntityActiveJob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.EntityLevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;

public class MilkHusbandryMobLevelJob
extends EntityLevelJob<HusbandryMob> {
    public SettlementHusbandryZone zone;

    public MilkHusbandryMobLevelJob(HusbandryMob target, SettlementHusbandryZone zone) {
        super(target);
        this.zone = zone;
    }

    public MilkHusbandryMobLevelJob(LoadData save) {
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
        return this.zone.containsTile(((HusbandryMob)this.target).getTileX(), ((HusbandryMob)this.target).getTileY());
    }

    public static <T extends MilkHusbandryMobLevelJob> JobSequence getJobSequence(EntityJobWorker worker, final FoundJob<T> foundJob) {
        final InventoryItem milkingItem = new InventoryItem("bucket");
        if (((HusbandryMob)((MilkHusbandryMobLevelJob)foundJob.job).target).canMilk(milkingItem)) {
            GameMessage targetDescription = ((HusbandryMob)((MilkHusbandryMobLevelJob)foundJob.job).target).getLocalization();
            LocalMessage activityDescription = new LocalMessage("activities", "milking", "target", targetDescription);
            final GameLinkedListJobSequence sequence = new GameLinkedListJobSequence(activityDescription);
            sequence.add(new InteractMobActiveJob<HusbandryMob>(worker, foundJob.priority, (HusbandryMob)((MilkHusbandryMobLevelJob)foundJob.job).target, m -> !((MilkHusbandryMobLevelJob)foundJob.job).isRemoved() && ((MilkHusbandryMobLevelJob)foundJob.job).isValid() && m.canMilk(milkingItem), ((MilkHusbandryMobLevelJob)foundJob.job).reservable, milkingItem){

                @Override
                public ActiveJobResult onInteracted(HusbandryMob target) {
                    ArrayList<InventoryItem> products = new ArrayList<InventoryItem>();
                    target.onMilk(milkingItem, products);
                    ArrayList<ItemPickupEntity> pickups = new ArrayList<ItemPickupEntity>(products.size());
                    for (InventoryItem product : products) {
                        ItemPickupEntity pickupItem = product.getPickupEntity(target.getLevel(), target.x, target.y);
                        target.getLevel().entityManager.pickups.add(pickupItem);
                        pickups.add(pickupItem);
                    }
                    PickupItemEntityActiveJob.addItemPickupJobs(this.worker, foundJob.priority, pickups, sequence);
                    return ActiveJobResult.FINISHED;
                }
            });
            return sequence;
        }
        return null;
    }

    public static JobTypeHandler.JobStreamSupplier<? extends MilkHusbandryMobLevelJob> getJobStreamer() {
        return (worker, handler) -> {
            Mob mob = worker.getMobWorker();
            ZoneTester restrictZone = worker.getJobRestrictZone();
            return mob.getLevel().entityManager.mobs.streamInRegionsShape(worker.getJobSearchBounds(), 0).filter(m -> !m.removed() && mob.isSamePlace((Entity)m) && m instanceof HusbandryMob).filter(m -> restrictZone.containsTile(m.getTileX(), m.getTileY())).map(m -> ((HusbandryMob)m).milkJob).filter(Objects::nonNull).filter(MilkHusbandryMobLevelJob::isValid);
        };
    }
}

