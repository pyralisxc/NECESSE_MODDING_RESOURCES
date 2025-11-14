/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.StartExpeditionActiveJob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class StartExpeditionLevelJob
extends LevelJob {
    public JobTypeHandler.SubHandler<?> handler;
    public Predicate<SettlerExpedition> canDoExpedition;
    public long shopSeed;

    public StartExpeditionLevelJob(int tileX, int tileY, JobTypeHandler.SubHandler<?> handler, Predicate<SettlerExpedition> canDoExpedition, long shopSeed) {
        super(tileX, tileY);
        this.handler = handler;
        this.canDoExpedition = canDoExpedition;
        this.shopSeed = shopSeed;
    }

    public StartExpeditionLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isWithinRestrictZone(ZoneTester zone) {
        return true;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isSameJob(LevelJob other) {
        return other.getID() == this.getID();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int getAfterPrioritizedPriority() {
        return 1000;
    }

    public static <T extends StartExpeditionLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        Mob mob = worker.getMobWorker();
        Level level = mob.getLevel();
        if (!(mob instanceof SettlerMob)) {
            return null;
        }
        SettlerMob settlerMob = (SettlerMob)((Object)mob);
        if (!settlerMob.isSettlerWithinSettlement()) {
            return null;
        }
        ServerSettlementData settlement = settlerMob.getSettlerSettlementServerData();
        if (settlement == null) {
            return null;
        }
        Point missionBoardTile = settlement.getMissionBoardTile();
        if (missionBoardTile == null) {
            return null;
        }
        for (SettlementMissionBoardMission mission : settlement.missionBoardManager.getMissions()) {
            if (!((StartExpeditionLevelJob)foundJob.job).canDoExpedition.test(mission.expedition) || !mission.allSettlersAssigned && !mission.assignedSettlers.contains(mob.getUniqueID()) || !mission.condition.isConditionMet(worker, settlement)) continue;
            GameMessage typedName = mission.expedition.getFullDisplayName();
            LocalMessage activityDescription = new LocalMessage("activities", "goingmission", "mission", typedName);
            LinkedListJobSequence sequence = new LinkedListJobSequence(activityDescription);
            int currentCost = mission.expedition.getCurrentCost(settlement, ((StartExpeditionLevelJob)foundJob.job).shopSeed);
            if (currentCost != 0) {
                int foundCost = 0;
                int coinID = ItemRegistry.getItemID("coin");
                WorkInventory workInventory = worker.getWorkInventory();
                for (InventoryItem item : workInventory.items()) {
                    if (item.item.getID() != coinID || (foundCost += item.getAmount()) < currentCost) continue;
                    break;
                }
                if (foundCost < currentCost) {
                    SettlementStorageRecords storageRecords = PickupSettlementStorageActiveJob.getStorageRecords(worker);
                    if (storageRecords == null) continue;
                    int missingCost = currentCost - foundCost;
                    LinkedList<SettlementStoragePickupSlot> foundItems = storageRecords.getIndex(SettlementStorageItemIDIndex.class).findPickupSlots(coinID, worker, null, missingCost, missingCost);
                    if (foundItems == null) continue;
                    for (SettlementStoragePickupSlot foundItem : foundItems) {
                        sequence.add(foundItem.toPickupJob(worker, foundJob.priority));
                    }
                }
            }
            sequence.add(new StartExpeditionActiveJob(worker, foundJob.priority, mission, currentCost));
            worker.setPrioritizeNextJob(HasStorageLevelJob.class, false);
            return sequence;
        }
        return null;
    }

    public static JobTypeHandler.JobStreamSupplier<? extends StartExpeditionLevelJob> getJobStreamer(HumanMob humanMob) {
        return (worker, handler) -> {
            Mob mobWorker = worker.getMobWorker();
            return Stream.of(new StartExpeditionLevelJob(mobWorker.getTileX(), mobWorker.getTileY(), handler, humanMob::canDoExpedition, humanMob.getShopSeed()));
        };
    }
}

